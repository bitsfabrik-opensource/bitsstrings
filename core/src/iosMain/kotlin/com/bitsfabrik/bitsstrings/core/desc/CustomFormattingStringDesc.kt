package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale

actual data class CustomFormattingStringDesc<T> actual constructor(
    val obj: T,
    val formatter: CustomFormatter<T>
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        val locale = localeType.locale
        return formatter.format(obj, locale.toBitsLocale())
    }
}