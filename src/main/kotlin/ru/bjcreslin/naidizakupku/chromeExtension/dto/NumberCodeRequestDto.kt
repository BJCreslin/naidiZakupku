package ru.bjcreslin.naidizakupku.chromeExtension.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class NumberCodeRequestDto(
    @field:Pattern(regexp = "^\\d{3,6}$", message = "Number code must be 3-6 digits")
    private val numberCode: String
) {
    fun getNumberCode(): Int = numberCode.toInt()
    
    fun getNumberCodeString(): String = numberCode
}
