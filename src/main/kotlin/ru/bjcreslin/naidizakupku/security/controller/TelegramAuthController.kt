package ru.bjcreslin.naidizakupku.security.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.bjcreslin.naidizakupku.security.dto.*
import ru.bjcreslin.naidizakupku.security.service.TelegramAuthService
import ru.bjcreslin.naidizakupku.security.service.UserSessionService

@RestController
@RequestMapping("/api/auth/telegram")
class TelegramAuthController(
    private val telegramAuthService: TelegramAuthService,
    private val userSessionService: UserSessionService
) {
    
    private val logger = LoggerFactory.getLogger(TelegramAuthController::class.java)
    
    @PostMapping("/validate")
    fun validateTelegramData(@Valid @RequestBody request: TelegramAuthRequest): ResponseEntity<TelegramAuthResponse> {
        logger.info("Validating Telegram data for user: ${request.id}")
        
        val isValid = telegramAuthService.validateTelegramData(request)
        
        if (!isValid) {
            return ResponseEntity.badRequest().body(
                TelegramAuthResponse(
                    success = false,
                    message = "Invalid Telegram data"
                )
            )
        }
        
        val userInfo = telegramAuthService.extractUserInfo(request)
        val session = userSessionService.createSession(userInfo)
        val token = userSessionService.createJwtToken(session)
        
        return ResponseEntity.ok(
            TelegramAuthResponse(
                success = true,
                sessionId = session.sessionId,
                token = token,
                user = userInfo
            )
        )
    }
    
    @PostMapping("/webapp")
    fun handleWebAppData(@Valid @RequestBody request: TelegramAuthRequest): ResponseEntity<TelegramAuthResponse> {
        logger.info("Processing Web App data for user: ${request.id}")
        
        // Для Web App используем ту же логику валидации
        return validateTelegramData(request)
    }
    
    @GetMapping("/session/{sessionId}")
    fun validateSession(@PathVariable sessionId: String): ResponseEntity<SessionValidationResponse> {
        logger.info("Validating session: $sessionId")
        
        val session = userSessionService.validateSession(sessionId)
        
        if (session == null) {
            return ResponseEntity.ok(
                SessionValidationResponse(
                    valid = false,
                    message = "Session not found or expired"
                )
            )
        }
        
        val userInfo = TelegramUserInfo(
            id = session.telegramId,
            username = session.user.username,
            firstName = session.user.username, // Упрощенно, можно добавить поля в User
            lastName = null,
            photoUrl = null
        )
        
        return ResponseEntity.ok(
            SessionValidationResponse(
                valid = true,
                user = userInfo
            )
        )
    }
    
    @DeleteMapping("/logout")
    fun logout(@RequestParam sessionId: String): ResponseEntity<Map<String, String>> {
        logger.info("Logging out session: $sessionId")
        
        val success = userSessionService.invalidateSession(sessionId)
        
        return if (success) {
            ResponseEntity.ok(mapOf("message" to "Successfully logged out"))
        } else {
            ResponseEntity.badRequest().body(mapOf("message" to "Session not found"))
        }
    }
    
    @DeleteMapping("/logout/all")
    fun logoutAll(@RequestParam telegramId: Long): ResponseEntity<Map<String, String>> {
        logger.info("Logging out all sessions for user: $telegramId")
        
        val count = userSessionService.invalidateAllUserSessions(telegramId)
        
        return ResponseEntity.ok(mapOf("message" to "Logged out from $count sessions"))
    }
}
