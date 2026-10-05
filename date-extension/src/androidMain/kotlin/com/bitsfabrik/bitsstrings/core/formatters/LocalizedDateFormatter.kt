package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

actual class LocalizedDateFormatter(
    private val pattern : String,
    private val locale : Locale = Locale.getDefault()
) {

    private val formatter by lazy {
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(locale)
    }

    actual fun format(date: LocalDate): String{
        return formatter.format(date.toJavaLocalDate())
    }

    actual fun withLocale(locale: BitsLocale) : LocalizedDateFormatter {
        return LocalizedDateFormatter(
            pattern,
            locale.toPlatformLocale()
        )
    }

    actual companion object {
        actual fun ofPattern(pattern : String): LocalizedDateFormatter{
            return LocalizedDateFormatter(pattern)
        }
    }
}