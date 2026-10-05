package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.localTimeZone
import platform.Foundation.timeZoneForSecondsFromGMT
import platform.Foundation.timeZoneWithName
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual class LocalizedDateTimeFormatter(
    private val pattern : String,
    private val locale: NSLocale = NSLocale.Companion.currentLocale
) {

    private val utcTimeZone = NSTimeZone.timeZoneWithName("UTC")
        ?: NSTimeZone.timeZoneForSecondsFromGMT(0)

    private val formatter by lazy {
        NSDateFormatter().apply {
            this@apply.locale = this@LocalizedDateTimeFormatter.locale
            this.dateFormat = pattern
            this.timeZone = utcTimeZone
        }
    }

    actual fun format(dateTime: LocalDateTime): String{
        return formatter.stringFromDate(dateTime.toInstant(TimeZone.UTC).toNSDate())
    }

    actual fun withLocale(locale: BitsLocale) : LocalizedDateTimeFormatter {
        return LocalizedDateTimeFormatter(
            pattern,
            locale.toPlatformLocale()
        )
    }

    actual companion object {
        actual fun ofPattern(pattern : String): LocalizedDateTimeFormatter{
            return LocalizedDateTimeFormatter(pattern)
        }
    }
}