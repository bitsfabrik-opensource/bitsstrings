package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.res.PluralsResource
import com.bitsfabrik.bitsstrings.core.utils.Utils.localizedString
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.create

actual data class PluralStringDesc actual constructor(
    val pluralsRes: PluralsResource,
    val number: Int
) : StringDesc {
    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        val string = localizedString(
            pluralsRes.resourceId,
            localeType = localeType
        )

        @OptIn(BetaInteropApi::class)
        @Suppress("CAST_NEVER_SUCCEEDS")
        return NSString.create(
            format = string,
            locale = localeType.locale,
            args = arrayOf(number)
        ) as String
    }
}
