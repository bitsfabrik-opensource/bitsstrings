package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.formatters.LocalizedDateFormatter
import com.bitsfabrik.bitsstrings.core.formatters.LocalizedDateTimeFormatter
import com.bitsfabrik.bitsstrings.core.formatters.LocalizedInstantFormatter
import com.bitsfabrik.bitsstrings.core.formatters.LocalizedTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Suppress("FunctionName")
fun StringDesc.Companion.Instant(instant: Instant, formatter: InstantFormatter) =
    InstantStringDesc(instant) { i, l ->
        formatter.format(i, l)
    }

@OptIn(ExperimentalTime::class)
@Suppress("FunctionName")
fun StringDesc.Companion.Instant(instant: Instant, pattern: String) =
    InstantStringDesc(instant) { i, l ->
        LocalizedInstantFormatter.ofPattern(pattern).withLocale(l).format(i)
    }

@OptIn(ExperimentalTime::class)
expect class InstantStringDesc(instant: Instant, formatter: InstantFormatter) : StringDesc

fun interface InstantFormatter {
    @OptIn(ExperimentalTime::class)
    fun format(instant: Instant, locale: BitsLocale): String
}