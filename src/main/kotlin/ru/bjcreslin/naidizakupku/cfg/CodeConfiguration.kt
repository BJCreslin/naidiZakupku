package ru.bjcreslin.naidizakupku.cfg

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * класс для хранения свойст генерации кодов
 */
@ConfigurationProperties(prefix = "code")
@Component
data class CodeConfiguration(
    /**
     * Минимальное значение генерации
     */
    var minimum: Int = 1_000,
    /**
     * Максимальное значение генерации
     */
    var maximum: Int = 1_000_000,
    /**
     * Максимальная длительность действия сгенерированного кода в минутах
     */
    val maxAttemptMinutes: Long = 3
)
