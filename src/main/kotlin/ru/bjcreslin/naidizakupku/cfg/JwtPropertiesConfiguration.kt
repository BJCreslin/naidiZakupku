package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * класс для хранения свойст токенов
 */
@ConfigurationProperties(prefix = "jwt.token")
@Component
data class JwtPropertiesConfiguration(
    var expired: Long = 3000000,
    var secret: String = "",
    var header: String = "",
    var bearerPrefix: String = "Bearer"
)
