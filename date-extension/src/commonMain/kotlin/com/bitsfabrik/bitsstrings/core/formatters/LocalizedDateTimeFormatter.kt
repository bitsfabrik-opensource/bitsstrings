package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import kotlinx.datetime.LocalDateTime

expect class LocalizedDateTimeFormatter {
    fun format(dateTime: LocalDateTime): String

    fun withLocale(locale : BitsLocale) : LocalizedDateTimeFormatter

    companion object {
        // Factory method to get the platform-specific implementation
        fun ofPattern(pattern : String = "dd.MM.yyyy hh:mm"): LocalizedDateTimeFormatter
    }
}