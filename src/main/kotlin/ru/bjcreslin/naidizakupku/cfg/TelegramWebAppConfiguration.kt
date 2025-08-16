package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "telegram.webapp")
@Component
data class TelegramWebAppConfiguration(
    var baseUrl: String = "https://naidizakupku.ru",
    var allowedOrigins: String = "https://naidizakupku.ru,http://localhost:3000"
) {
    fun getAllowedOriginsList(): List<String> {
        return allowedOrigins.split(",").map { it.trim() }
    }
}

@ConfigurationProperties(prefix = "telegram.auth")
@Component
data class TelegramAuthConfiguration(
    var sessionDurationHours: Long = 24
)
