package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import kotlinx.datetime.LocalDate

expect class LocalizedDateFormatter {
    fun format(date: LocalDate): String

    fun withLocale(locale : BitsLocale) : LocalizedDateFormatter

    companion object {
        // Factory method to get the platform-specific implementation
        fun ofPattern(pattern : String = "dd.MM.yyyy"): LocalizedDateFormatter
    }
}