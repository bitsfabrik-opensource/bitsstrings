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
fun StringDesc.Companion.Date(date: LocalDate, formatter: DateFormatter) =
    DateStringDesc(date) { d, l ->
        formatter.format(d, l)
    }

@Suppress("FunctionName")
fun StringDesc.Companion.Date(date: LocalDate, pattern: String) =
    DateStringDesc(date) { d, l ->
        LocalizedDateFormatter.ofPattern(pattern).withLocale(l).format(d)
    }


fun interface DateFormatter {
    fun format(date: LocalDate, locale: BitsLocale): String
}

expect class DateStringDesc(date: LocalDate, formatter: DateFormatter) : StringDesc