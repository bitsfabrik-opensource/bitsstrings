package com.bitsfabrik.bitsstrings.core.formatters

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual class LocalizedDateTimeFormatter(
    private val pattern : String,
    private val locale : Locale = Locale.getDefault()
) {

    private val formatter by lazy {
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(locale)
    }

    actual fun format(dateTime: LocalDateTime): String{
        return formatter.format(dateTime.toJavaLocalDateTime())
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