package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.defaultTimeZone
import platform.Foundation.timeZoneWithName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
actual class LocalizedInstantFormatter(
    private val pattern : String,
    private val locale: NSLocale = NSLocale.currentLocale
) {

    private val formatter by lazy {
        NSDateFormatter().apply {
            this@apply.locale = this@LocalizedInstantFormatter.locale
            dateFormat = pattern
        }
    }

    actual fun format(instant: Instant, timeZone: TimeZone): String{
        formatter.timeZone = NSTimeZone.timeZoneWithName(timeZone.id) ?: NSTimeZone.defaultTimeZone()
        return formatter.stringFromDate(instant.toNSDate())
    }

    actual fun withLocale(locale: BitsLocale) : LocalizedInstantFormatter {
        return LocalizedInstantFormatter(
            pattern,
            locale.toPlatformLocale()
        )
    }

    actual companion object {
        actual fun ofPattern(pattern : String): LocalizedInstantFormatter{
            return LocalizedInstantFormatter(pattern)
        }
    }
}