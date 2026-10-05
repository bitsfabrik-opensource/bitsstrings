package com.bitsfabrik.bitsstrings.core.utils

import com.bitsfabrik.bitsstrings.core.BitsLocale
import java.util.Locale


fun Locale.toBitsLocale() = BitsLocale(
    language,
    country.takeIf { it.isNotEmpty() },
    variant.takeIf { it.isNotEmpty() }
)

fun BitsLocale.toPlatformLocale(): Locale {
    val builder = Locale.Builder()
    builder.setLanguage(language)
    if (country != null) {
        builder.setRegion(country)
    }
    if (variant != null) {
        builder.setVariant(variant)
    }
    return builder.build()
}
