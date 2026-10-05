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
fun StringDesc.Companion.DateTime(dateTime: LocalDateTime, formatter: DateTimeFormatter) =
    DateTimeStringDesc(dateTime) { dt, l ->
        formatter.format(dt, l)
    }

@Suppress("FunctionName")
fun StringDesc.Companion.DateTime(dateTime: LocalDateTime, pattern: String) =
    DateTimeStringDesc(dateTime) { dt, l ->
        LocalizedDateTimeFormatter.ofPattern(pattern).withLocale(l).format(dt)
    }

expect class DateTimeStringDesc(dateTime: LocalDateTime, formatter: DateTimeFormatter) : StringDesc

fun interface DateTimeFormatter {
    fun format(dateTime: LocalDateTime, locale: BitsLocale): String
}
