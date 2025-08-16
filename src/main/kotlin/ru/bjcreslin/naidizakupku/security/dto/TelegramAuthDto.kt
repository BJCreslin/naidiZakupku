package ru.bjcreslin.naidizakupku.security.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TelegramAuthRequest(
    @field:NotNull
    @JsonProperty("id")
    val id: Long,
    
    @field:NotBlank
    @JsonProperty("first_name")
    val firstName: String,
    
    @JsonProperty("last_name")
    val lastName: String? = null,
    
    @JsonProperty("username")
    val username: String? = null,
    
    @JsonProperty("photo_url")
    val photoUrl: String? = null,
    
    @field:NotNull
    @JsonProperty("auth_date")
    val authDate: Long,
    
    @field:NotBlank
    @JsonProperty("hash")
    val hash: String
)

data class TelegramAuthResponse(
    val success: Boolean,
    val sessionId: String? = null,
    val token: String? = null,
    val message: String? = null,
    val user: TelegramUserInfo? = null
)

data class TelegramUserInfo(
    val id: Long,
    val username: String?,
    val firstName: String,
    val lastName: String?,
    val photoUrl: String?
)

data class SessionValidationRequest(
    @field:NotBlank
    val sessionId: String
)

data class SessionValidationResponse(
    val valid: Boolean,
    val user: TelegramUserInfo? = null,
    val message: String? = null
)
