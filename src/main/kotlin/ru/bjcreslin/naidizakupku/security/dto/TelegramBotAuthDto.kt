package ru.bjcreslin.naidizakupku.security.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max

// Request Models
data class QrCodeRequest(
    @field:NotBlank(message = "Bot URL is required")
    val botUrl: String
)

data class LoginRequest(
    @field:NotNull(message = "Code is required")
    @field:Min(value = 100000, message = "Code must be at least 100000")
    @field:Max(value = 999999, message = "Code must be at most 999999")
    val code: Int
)

// Response Models
data class BotInfoResponse(
    val success: Boolean,
    val botInfo: BotInfo? = null,
    val error: String? = null
)

data class BotInfo(
    val botUsername: String,
    val botUrl: String
)

data class QrCodeResponse(
    val success: Boolean,
    val qrCodeUrl: String? = null,
    val error: String? = null
)

data class LoginResponse(
    val success: Boolean,
    val session: AuthSession? = null,
    val token: String? = null,
    val error: String? = null
)

data class AuthSession(
    val sessionId: String,
    val telegramId: Long,
    val username: String?,
    val firstName: String,
    val lastName: String?,
    val photoUrl: String?,
    val isActive: Boolean,
    val createdAt: String,
    val lastActivityAt: String
)
