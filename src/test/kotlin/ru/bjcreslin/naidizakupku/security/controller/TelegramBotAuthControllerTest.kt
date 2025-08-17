package ru.bjcreslin.naidizakupku.security.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import ru.bjcreslin.naidizakupku.security.dto.BotInfo
import ru.bjcreslin.naidizakupku.security.service.TelegramBotAuthService
import ru.bjcreslin.naidizakupku.security.exceptions.InvalidCodeException

import org.mockito.Mockito.`when`
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TelegramBotAuthController::class)
class TelegramBotAuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var telegramBotAuthService: TelegramBotAuthService

    @Test
    fun `getBotInfo should return bot information`() {
        // Given
        val botInfo = BotInfo(
            botUsername = "test_bot",
            botUrl = "https://t.me/test_bot"
        )
        `when`(telegramBotAuthService.getBotInfo()).thenReturn(botInfo)

        // When & Then
        mockMvc.perform(get("/api/auth/telegram-bot/info"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.botInfo.botUsername").value("test_bot"))
            .andExpect(jsonPath("$.botInfo.botUrl").value("https://t.me/test_bot"))
    }

    @Test
    fun `generateQrCode should return QR code URL`() {
        // Given
        val botUrl = "https://t.me/test_bot"
        val qrCodeUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
        `when`(telegramBotAuthService.generateQrCode(botUrl)).thenReturn(qrCodeUrl)

        // When & Then
        mockMvc.perform(
            post("/api/auth/telegram-bot/qr-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"botUrl": "$botUrl"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.qrCodeUrl").value(qrCodeUrl))
    }

    @Test
    fun `loginWithCode should return success response for valid code`() {
        // Given
        val code = 123456
        val session = ru.bjcreslin.naidizakupku.security.dto.AuthSession(
            sessionId = "session_123",
            telegramId = 123456789L,
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            photoUrl = null,
            isActive = true,
            createdAt = "2024-01-01T12:00:00Z",
            lastActivityAt = "2024-01-01T12:00:00Z"
        )
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        val authResult = ru.bjcreslin.naidizakupku.security.service.AuthResult(session, token)
        
        `when`(telegramBotAuthService.authenticateWithCode(code)).thenReturn(authResult)

        // When & Then
        mockMvc.perform(
            post("/api/auth/telegram-bot/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code": $code}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.session.sessionId").value("session_123"))
            .andExpect(jsonPath("$.token").value(token))
    }

    @Test
    fun `loginWithCode should return error for invalid code`() {
        // Given
        val code = 999999
        `when`(telegramBotAuthService.authenticateWithCode(code))
            .thenThrow(InvalidCodeException("Код не найден или недействителен"))

        // When & Then
        mockMvc.perform(
            post("/api/auth/telegram-bot/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code": $code}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Неверный код или время истекло"))
    }

    @Test
    fun `generateQrCode should return error for invalid request`() {
        // When & Then
        mockMvc.perform(
            post("/api/auth/telegram-bot/qr-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"botUrl": ""}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `loginWithCode should return error for invalid code format`() {
        // When & Then
        mockMvc.perform(
            post("/api/auth/telegram-bot/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"code": 12345}""")
        )
            .andExpect(status().isBadRequest)
    }
}
