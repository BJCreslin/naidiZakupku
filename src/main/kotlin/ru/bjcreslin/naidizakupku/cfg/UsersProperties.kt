package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * класс для хранения свойст пользователей
 */
@ConfigurationProperties(prefix = "users")
@Component
data class UsersProperties(
    var defaultRole: String = "User"
)
