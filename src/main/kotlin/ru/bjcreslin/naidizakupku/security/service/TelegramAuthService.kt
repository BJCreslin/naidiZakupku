package ru.bjcreslin.naidizakupku.security.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration
import ru.bjcreslin.naidizakupku.security.dto.TelegramAuthRequest
import ru.bjcreslin.naidizakupku.security.dto.TelegramUserInfo
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidTokenException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class TelegramAuthService(
    private val botConfiguration: BotConfiguration
) {
    
    private val logger = LoggerFactory.getLogger(TelegramAuthService::class.java)
    
    fun validateTelegramData(request: TelegramAuthRequest): Boolean {
        try {
            // Проверяем, что данные не устарели (не старше 24 часов)
            val currentTime = System.currentTimeMillis() / 1000
            if (currentTime - request.authDate > 86400) {
                logger.warn("Telegram auth data is too old: ${request.authDate}")
                return false
            }
            
            // Создаем строку для проверки
            val dataCheckString = buildDataCheckString(request)
            
            // Вычисляем HMAC-SHA-256
            val secretKey = SecretKeySpec("WebAppData".toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(secretKey)
            
            val calculatedHash = mac.doFinal(dataCheckString.toByteArray(StandardCharsets.UTF_8))
            val calculatedHashHex = bytesToHex(calculatedHash)
            
            // Сравниваем с полученным хешем
            val isValid = calculatedHashHex == request.hash
            
            if (!isValid) {
                logger.warn("Invalid Telegram hash. Expected: $calculatedHashHex, got: ${request.hash}")
            }
            
            return isValid
            
        } catch (e: Exception) {
            logger.error("Error validating Telegram data", e)
            return false
        }
    }
    
    private fun buildDataCheckString(request: TelegramAuthRequest): String {
        val params = mutableListOf<Pair<String, String>>()
        
        params.add("auth_date" to request.authDate.toString())
        params.add("first_name" to request.firstName)
        params.add("id" to request.id.toString())
        
        request.lastName?.let { params.add("last_name" to it) }
        request.photoUrl?.let { params.add("photo_url" to it) }
        request.username?.let { params.add("username" to it) }
        
        // Сортируем параметры по ключу
        params.sortBy { it.first }
        
        // Создаем строку в формате key=value\n
        return params.joinToString("\n") { "${it.first}=${it.second}" }
    }
    
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789abcdef"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789abcdef"[v and 0x0F]
        }
        return String(hexChars)
    }
    
    fun extractUserInfo(request: TelegramAuthRequest): TelegramUserInfo {
        return TelegramUserInfo(
            id = request.id,
            username = request.username,
            firstName = request.firstName,
            lastName = request.lastName,
            photoUrl = request.photoUrl
        )
    }
}
