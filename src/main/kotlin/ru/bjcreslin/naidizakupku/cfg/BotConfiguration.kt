package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * класс для хранения свойст телеграм бота
 */
@ConfigurationProperties(prefix = "telergram")
@Component
data class BotConfiguration(
    var token: String = "",

    var name: String = ""
)
