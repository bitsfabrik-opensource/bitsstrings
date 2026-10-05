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


@Suppress("FunctionName")
fun StringDesc.Companion.Time(time: LocalTime, formatter: TimeFormatter) =
    TimeStringDesc(time) { t, l ->
        formatter.format(t, l)
    }

@Suppress("FunctionName")
fun StringDesc.Companion.Time(time: LocalTime, pattern: String) =
    TimeStringDesc(time) { t, l ->
        LocalizedTimeFormatter.ofPattern(pattern).withLocale(l).format(t)
    }

expect class TimeStringDesc(time: LocalTime, formatter: TimeFormatter) : StringDesc

fun interface TimeFormatter {
    fun format(time: LocalTime, locale: BitsLocale): String
}
