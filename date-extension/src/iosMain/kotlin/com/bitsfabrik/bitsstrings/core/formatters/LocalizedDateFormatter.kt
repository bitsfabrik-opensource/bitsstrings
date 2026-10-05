package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneWithName
import platform.Foundation.timeZoneForSecondsFromGMT
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual class LocalizedDateFormatter(
    private val pattern: String,
    private val locale: NSLocale = NSLocale.Companion.currentLocale
) {

    // 1. Define a neutral timezone (UTC)
    private val utcTimeZone = NSTimeZone.timeZoneWithName("UTC")
        ?: NSTimeZone.timeZoneForSecondsFromGMT(0)

    private val formatter by lazy {
        NSDateFormatter().apply {
            this.locale = this@LocalizedDateFormatter.locale
            this.dateFormat = pattern
            this.timeZone = utcTimeZone
        }
    }

    actual fun format(date: LocalDate): String {
        // 3. Create a Gregorian calendar forced to UTC
        val calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierGregorian)!!
        calendar.timeZone = utcTimeZone

        // 4. Convert components to NSDate using the UTC calendar
        val nsDate = calendar.dateFromComponents(date.toNSDateComponents())
            ?: return ""

        return formatter.stringFromDate(nsDate)
    }

    actual fun withLocale(locale: BitsLocale): LocalizedDateFormatter {
        return LocalizedDateFormatter(
            pattern,
            locale.toPlatformLocale()
        )
    }

    actual companion object {
        actual fun ofPattern(pattern: String): LocalizedDateFormatter {
            return LocalizedDateFormatter(pattern)
        }
    }
}

