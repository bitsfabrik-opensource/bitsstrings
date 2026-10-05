package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual class LocalizedTimeFormatter(
    private val pattern : String,
    private val locale : Locale = Locale.getDefault()
) {

    private val formatter by lazy {
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(locale)
    }

    actual fun format(time: LocalTime): String{
        return formatter.format(time.toJavaLocalTime())
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