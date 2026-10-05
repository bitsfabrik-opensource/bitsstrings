package com.bitsfabrik.bitsstrings.core

import com.bitsfabrik.bitsstrings.core.formatters.LocalizedDateFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LocalizedDateFormatterTestAndroid {

    private val dateFormatter : LocalizedDateFormatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")

    @Test
    fun example() {
        val date = LocalDate(2026, 1, 2)

        val expected = "%02d.%02d.%04d".format(date.day, date.month.number, date.year)
        assertEquals(expected, dateFormatter.format(date))
    }
}