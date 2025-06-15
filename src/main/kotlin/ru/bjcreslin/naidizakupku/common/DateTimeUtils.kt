package ru.bjcreslin.naidizakupku.common

import java.time.LocalDateTime
import java.util.*

class DateTimeUtils {

    companion object {

        fun getCurrentDate(): Date {
            return Date()
        }

        fun getCurrentDateTime(): LocalDateTime {
            return LocalDateTime.now()
        }
    }
}