package ru.bjcreslin.naidizakupku.security.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.bjcreslin.naidizakupku.cfg.BotConfiguration
import ru.bjcreslin.naidizakupku.security.dto.*
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidCodeException
import ru.bjcreslin.naidizakupku.codeService.TelegramCodeService
import ru.bjcreslin.naidizakupku.telegramUser.TelegramUserService
import ru.bjcreslin.naidizakupku.user.entity.User
import ru.bjcreslin.naidizakupku.telegramUser.entity.TelegramUser
import ru.bjcreslin.naidizakupku.telegramUser.repository.TelegramUserRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

@Service
class TelegramBotAuthService(
    private val botConfiguration: BotConfiguration,
    private val telegramCodeService: TelegramCodeService,
    private val telegramUserService: TelegramUserService,
    private val telegramUserRepository: TelegramUserRepository,
    private val userSessionService: UserSessionService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    
    private val logger = LoggerFactory.getLogger(TelegramBotAuthService::class.java)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    fun getBotInfo(): BotInfo {
        val botUsername = getBotUsername()
        return BotInfo(
            botUsername = botUsername,
            botUrl = "https://t.me/$botUsername"
        )
    }
    
    fun generateQrCode(botUrl: String): String {
        try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(botUrl, BarcodeFormat.QR_CODE, 256, 256)
            
            val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
            val baos = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "PNG", baos)
            val imageBytes = baos.toByteArray()
            
            return "data:image/png;base64,${Base64.getEncoder().encodeToString(imageBytes)}"
        } catch (e: Exception) {
            logger.error("Error generating QR code", e)
            throw RuntimeException("Failed to generate QR code", e)
        }
    }
    
    fun authenticateWithCode(code: Int): AuthResult {
        logger.info("Authenticating with code: $code")
        
        // Получаем пользователя по коду
        val user = telegramCodeService.getUserByCode(code)
            ?: throw InvalidCodeException("Код не найден или недействителен")
        
        // Получаем Telegram пользователя по User
        val telegramUser = telegramUserRepository.findByUser(user)
            ?: throw InvalidCodeException("Telegram пользователь не найден")
        
        // Создаем сессию
        val session = createSession(user, telegramUser)
        
        // Генерируем JWT токен
        val token = generateJwtToken(user)
        
        logger.info("Successfully authenticated user: ${user.username} with telegram ID: ${telegramUser.telegramId}")
        
        return AuthResult(session, token)
    }
    
    private fun getBotUsername(): String {
        val username = System.getenv("NAIDI_ZAKUPKU_TELEGRAM_BOT_NAME") ?: botConfiguration.name
        if (username.isBlank()) {
            logger.warn("Telegram bot username is not configured")
            return "your_bot_username"
        }
        return username
    }
    
    private fun createSession(user: User, telegramUser: TelegramUser): AuthSession {
        val sessionId = UUID.randomUUID().toString()
        val now = LocalDateTime.now()
        
        return AuthSession(
            sessionId = sessionId,
            telegramId = telegramUser.telegramId,
            username = user.username,
            firstName = user.username ?: "User",
            lastName = null,
            photoUrl = null,
            isActive = true,
            createdAt = now.format(dateFormatter),
            lastActivityAt = now.format(dateFormatter)
        )
    }
    
    private fun generateJwtToken(user: User): String {
        val roles = user.userRoles.map { it.role.name }
        return jwtTokenProvider.createAccessToken(user.username, roles)
    }
}

data class AuthResult(
    val session: AuthSession,
    val token: String
)
