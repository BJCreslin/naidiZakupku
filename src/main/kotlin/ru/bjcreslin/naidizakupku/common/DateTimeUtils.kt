package ru.bjcreslin.naidizakupku.common

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeUtils {

    companion object {

        fun getCurrentDate(): Date {
            return Date()
        }

        fun getCurrentDateTime(): LocalDateTime {
            return LocalDateTime.now()
        }

        fun getCurrentDateAsString(): String {
            val nowUtc = LocalDateTime.now(ZoneOffset.UTC)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            return nowUtc.format(formatter)
        }
    }
}