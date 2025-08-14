package ru.bjcreslin.naidizakupku.common.error.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ValidationErrorResponse(
    val code: String,
    val message: String,
    val fieldErrors: Map<String, String>,
    val path: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val traceId: String? = null
)
