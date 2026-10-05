package com.bitsfabrik.bitsstrings.core

import com.bitsfabrik.bitsstrings.core.formatters.LocalizedDateFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.NSTimeZone
import platform.Foundation.defaultTimeZone
import platform.Foundation.setDefaultTimeZone
import platform.Foundation.stringWithFormat
import platform.Foundation.timeZoneWithName
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LocalizedDateFormatterTestIos {

    private lateinit var originalTimeZone: NSTimeZone

    @BeforeTest
    fun setUp() {
        originalTimeZone = NSTimeZone.defaultTimeZone
    }

    @AfterTest
    fun tearDown() {
        NSTimeZone.setDefaultTimeZone(originalTimeZone)
    }

    /**
     * Bug reproduction: In a timezone west of UTC, formatting a
     * LocalDate must not slip to the previous day.
     *
     * If April 30, 2026, is internally constructed as 2026-04-30T00:00:00Z
     * and then evaluated using the system timezone (UTC-11), a faulty formatter
     * will return "29.04.2026" – causing the test to fail.
     */
    @Test
    fun formats_date_correctly_in_timezone_west_of_utc() {
        NSTimeZone.setDefaultTimeZone(
            NSTimeZone.timeZoneWithName("Pacific/Midway")!! // UTC-11
        )

        val formatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate(2026, 4, 30)

        assertEquals("30.04.2026", formatter.format(date))
    }

    /**
     * Opposite direction: A timezone east of UTC must not slip to the next day.
     */
    @Test
    fun formats_date_correctly_in_timezone_east_of_utc() {
        NSTimeZone.setDefaultTimeZone(
            NSTimeZone.timeZoneWithName("Pacific/Kiritimati")!! // UTC+14
        )

        val formatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate(2026, 4, 30)

        assertEquals("30.04.2026", formatter.format(date))
    }

    /**
     * Direct weekday test: April 30, 2026, is a Thursday.
     * With incorrect timezone handling, this would output "Mittwoch" (Wednesday).
     */
    @Test
    fun formats_weekday_correctly_in_timezone_west_of_utc() {
        NSTimeZone.setDefaultTimeZone(
            NSTimeZone.timeZoneWithName("Pacific/Midway")!!
        )

        val formatter = LocalizedDateFormatter("EEEE", NSLocale("de"))
        val date = LocalDate(2026, 4, 30) // Thursday

        assertEquals("Donnerstag", formatter.format(date))
    }

    /**
     * Edge case: First day of the month. With an off-by-one error caused by
     * the timezone, "01.05.2026" would become "30.04.2026" – meaning the
     * month and potentially the year would also be wrong.
     */
    @Test
    fun formats_first_of_month_correctly_in_timezone_west_of_utc() {
        NSTimeZone.setDefaultTimeZone(
            NSTimeZone.timeZoneWithName("Pacific/Midway")!!
        )

        val formatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate(2026, 5, 1)

        assertEquals("01.05.2026", formatter.format(date))
    }

    /**
     * Edge case: Turn of the year. The classic New Year's Eve problem.
     */
    @Test
    fun formats_new_years_day_correctly_in_timezone_west_of_utc() {
        NSTimeZone.setDefaultTimeZone(
            NSTimeZone.timeZoneWithName("Pacific/Midway")!!
        )

        val formatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate(2026, 1, 1)

        assertEquals("01.01.2026", formatter.format(date))
    }

    /**
     * Parameterized across multiple timezones – catches regressions
     * in case someone later reintroduces Calendar.current or
     * TimeZone.currentSystemDefault() somewhere.
     */
    @Test
    fun formats_date_correctly_across_all_relevant_timezones() {
        val timeZones = listOf(
            "Pacific/Midway",      // UTC-11
            "America/Los_Angeles", // UTC-8 / -7
            "America/New_York",    // UTC-5 / -4
            "UTC",
            "Europe/Berlin",       // UTC+1 / +2
            "Asia/Tokyo",          // UTC+9
            "Pacific/Auckland",    // UTC+12 / +13
            "Pacific/Kiritimati",  // UTC+14
        )

        val formatter = LocalizedDateFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate(2026, 4, 30)

        timeZones.forEach { tz ->
            NSTimeZone.setDefaultTimeZone(NSTimeZone.timeZoneWithName(tz)!!)
            assertEquals(
                "30.04.2026",
                formatter.format(date),
                "Incorrect formatting in timezone $tz",
            )
        }
    }
}