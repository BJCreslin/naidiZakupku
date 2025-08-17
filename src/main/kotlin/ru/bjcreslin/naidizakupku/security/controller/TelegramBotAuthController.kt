package ru.bjcreslin.naidizakupku.security.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.bjcreslin.naidizakupku.security.dto.*
import ru.bjcreslin.naidizakupku.security.service.TelegramBotAuthService
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidCodeException

@RestController
@RequestMapping("/api/auth/telegram-bot")
class TelegramBotAuthController(
    private val telegramBotAuthService: TelegramBotAuthService
) {
    
    private val logger = LoggerFactory.getLogger(TelegramBotAuthController::class.java)
    
    @GetMapping("/info")
    fun getBotInfo(): ResponseEntity<BotInfoResponse> {
        logger.info("Getting bot info")
        
        val botInfo = telegramBotAuthService.getBotInfo()
        return ResponseEntity.ok(BotInfoResponse(
            success = true,
            botInfo = botInfo
        ))
    }
    
    @PostMapping("/qr-code")
    fun generateQrCode(@Valid @RequestBody request: QrCodeRequest): ResponseEntity<QrCodeResponse> {
        logger.info("Generating QR code for bot URL: ${request.botUrl}")
        
        val qrCodeUrl = telegramBotAuthService.generateQrCode(request.botUrl)
        return ResponseEntity.ok(QrCodeResponse(
            success = true,
            qrCodeUrl = qrCodeUrl
        ))
    }
    
    @PostMapping("/login")
    fun loginWithCode(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        logger.info("Attempting login with code: ${request.code}")
        
        return try {
            val authResult = telegramBotAuthService.authenticateWithCode(request.code)
            ResponseEntity.ok(LoginResponse(
                success = true,
                session = authResult.session,
                token = authResult.token
            ))
        } catch (e: InvalidCodeException) {
            logger.warn("Invalid code attempt: ${request.code}, error: ${e.message}")
            ResponseEntity.badRequest().body(LoginResponse(
                success = false,
                error = "Неверный код или время истекло"
            ))
        } catch (e: Exception) {
            logger.error("Error during login with code: ${request.code}", e)
            ResponseEntity.internalServerError().body(LoginResponse(
                success = false,
                error = "Внутренняя ошибка сервера"
            ))
        }
    }
}
