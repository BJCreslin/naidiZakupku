package ru.bjcreslin.naidizakupku.chromeExtension.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class NumberCodeRequestDto(
    @field:Min(999)
    @field:Max(999999)
    val numberCode: Int
)
