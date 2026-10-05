package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.NSLocaleVariantCode
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.localeIdentifier
import platform.Foundation.localeIdentifierFromComponents
import platform.Foundation.variantCode

actual interface StringDesc {
    fun localized(): String
    fun localized(localeType: LocaleType = LocaleType.System): String

    actual sealed class LocaleType {
        abstract val locale: NSLocale
        abstract fun getLocaleBundle(rootBundle: NSBundle): NSBundle

        actual data object System : LocaleType() {
            override val locale: NSLocale
                get() = NSLocale.currentLocale

            override fun getLocaleBundle(rootBundle: NSBundle): NSBundle {
                return rootBundle
            }
        }

        actual class Custom actual constructor(locale: BitsLocale) : LocaleType() {

            constructor(nsLocale : NSLocale) : this(nsLocale.toBitsLocale())

            override val locale: NSLocale = locale.toPlatformLocale()

            override fun getLocaleBundle(rootBundle: NSBundle): NSBundle {
                // I don't like this, but I don't see a good way to get a proper BCP language tag from NSLocale
                // on Apple's dev documentation. This is a hack.
                val bcpLanguageTag = locale.localeIdentifier().replace("_", "-")
                return rootBundle.pathForResource(bcpLanguageTag, "lproj")
                    ?.let { NSBundle.bundleWithPath(it) }
                    ?: rootBundle
            }
        }


    }

    actual companion object {

    }
}