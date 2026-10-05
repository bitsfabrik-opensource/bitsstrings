package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
expect class LocalizedInstantFormatter {
    fun format(instant: Instant, timeZone: TimeZone = TimeZone.UTC): String

    fun withLocale(locale : BitsLocale) : LocalizedInstantFormatter

    companion object {
        // Factory method to get the platform-specific implementation
        fun ofPattern(pattern : String  = "dd.MM.yyyy hh:mm"): LocalizedInstantFormatter
    }
}
