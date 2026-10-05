package com.bitsfabrik.bitsstrings.core.utils

import com.bitsfabrik.bitsstrings.core.BitsLocale
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.NSLocaleVariantCode
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.Foundation.localeIdentifierFromComponents
import platform.Foundation.variantCode

fun NSLocale.toBitsLocale() = BitsLocale(
    languageCode,
    countryCode,
    variantCode,
)

fun BitsLocale.toPlatformLocale() = NSLocale(
    NSLocale.localeIdentifierFromComponents(
        buildMap {
            put(NSLocaleLanguageCode, language)
            country?.let { country ->
                put(NSLocaleCountryCode, country)
            }
            variant?.let { variant ->
                put(NSLocaleVariantCode, variant)
            }
        }
    )
)