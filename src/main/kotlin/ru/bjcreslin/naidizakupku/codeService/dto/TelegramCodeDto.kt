package ru.bjcreslin.naidizakupku.codeService.dto

data class TelegramCodeDto(
    /**
     * КОд
     */
    val code: Int,
    /**
     * Сколько минут действует код
     */
    val actionTimeMinutes: Long
)
