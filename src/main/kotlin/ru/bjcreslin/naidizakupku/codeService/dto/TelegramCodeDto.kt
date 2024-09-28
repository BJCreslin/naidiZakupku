package ru.bjcreslin.naidizakupku.codeService.dto

import java.time.LocalDateTime

data class TelegramCodeDto(
    /**
     * КОд
     */
    val code: Int,
    /**
     * Время последнего действия кода
     */
    val maxActionTime: LocalDateTime
)
