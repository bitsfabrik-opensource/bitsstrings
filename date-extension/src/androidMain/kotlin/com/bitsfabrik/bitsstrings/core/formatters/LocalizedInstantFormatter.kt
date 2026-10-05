package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant


@OptIn(ExperimentalTime::class)
actual class LocalizedInstantFormatter(
    private val pattern : String,
    private val locale : Locale = Locale.getDefault()
) {

    private val formatter by lazy {
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(locale)
    }

    actual fun format(instant: Instant, timeZone: TimeZone): String{
        val timeZoneCorrectedInstant = instant.toJavaInstant().atZone(timeZone.toJavaZoneId())
        return formatter.format(timeZoneCorrectedInstant)
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