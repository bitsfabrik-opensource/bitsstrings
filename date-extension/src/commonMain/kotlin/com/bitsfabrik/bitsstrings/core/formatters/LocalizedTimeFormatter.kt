package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import kotlinx.datetime.LocalTime

expect class LocalizedTimeFormatter {
    fun format(time: LocalTime): String

    fun withLocale(locale: BitsLocale): LocalizedTimeFormatter

    companion object {
        // Factory method to get the platform-specific implementation
        fun ofPattern(pattern: String = "hh:mm"): LocalizedTimeFormatter
    }
}