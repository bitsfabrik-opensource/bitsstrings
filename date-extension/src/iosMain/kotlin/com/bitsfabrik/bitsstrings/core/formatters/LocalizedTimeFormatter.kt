package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual class LocalizedTimeFormatter(
    private val pattern : String,
    private val locale: NSLocale = NSLocale.Companion.currentLocale
) {

    private val formatter by lazy {
        NSDateFormatter().apply {
            this@apply.locale = this@LocalizedTimeFormatter.locale
            dateFormat = pattern
        }
    }

    actual fun format(time: LocalTime): String{
        // Similar to formatDate, LocalTime needs to be associated with a date for NSDateFormatter
        val nsDate = time.atDate(LocalDate(2022, 1, 1)).toInstant(TimeZone.Companion.UTC).toNSDate()
        return formatter.stringFromDate(nsDate)
    }

    actual fun withLocale(locale: BitsLocale) : LocalizedTimeFormatter {
        return LocalizedTimeFormatter(
            pattern,
            locale.toPlatformLocale()
        )
    }

    actual companion object {
        actual fun ofPattern(pattern : String): LocalizedTimeFormatter{
            return LocalizedTimeFormatter(pattern)
        }
    }
}