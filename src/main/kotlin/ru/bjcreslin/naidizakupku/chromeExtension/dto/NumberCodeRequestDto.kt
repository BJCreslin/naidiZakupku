package ru.bjcreslin.naidizakupku.chromeExtension.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class NumberCodeRequestDto(
    @field:Min(1000)
    @field:Max(9999)
    val numberCode: Int
)
