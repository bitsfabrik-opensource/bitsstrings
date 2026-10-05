package com.bitsfabrik.bitsstrings.core.utils

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.desc.StringDesc
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.res.StringResource
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.stringWithFormat

object Utils {
    const val BASE_LOCALIZATION: String = "Base"

    fun processArgs(args: List<Any?>, localeType: LocaleType): Array<out Any?> {
        return args.map { (it as? StringDesc)?.localized(localeType) ?: it }.toTypedArray()
    }

    fun localizedString(resourceId: String, localeType: LocaleType): String {
        val mainBundle = NSBundle.mainBundle
        val bundle = localeType.getLocaleBundle(mainBundle)
        val stringInCurrentLocale = bundle.localizedStringForKey(
            key = resourceId,
            value = null,
            table = null
        )

        return if (stringInCurrentLocale == resourceId) {
            // String not found in localized bundle -> resourceId as value
            // Look in mainBundle
            val stringInDefaultBundle = mainBundle.localizedStringForKey(
                key = resourceId,
                value = null,
                table = null
            )

            if (stringInDefaultBundle == resourceId) {
                // String not found in main bundle -> use fallback locale
                val fallbackLocale = BitsLocale(mainBundle.developmentLocalization ?: BASE_LOCALIZATION)
                val fallbackLocaleBundle = LocaleType.Custom(fallbackLocale)
                    .getLocaleBundle(mainBundle)
                fallbackLocaleBundle.localizedStringForKey(
                    key = resourceId,
                    value = null,
                    table = null
                )
            } else {
                stringInDefaultBundle
            }
        } else {
            stringInCurrentLocale
        }
    }

}