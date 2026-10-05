package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.Language
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import platform.Foundation.languageCode

actual data class LocalizedRawStringDesc actual constructor(
    val strings: Map<Language, RawStringDesc>,
    val fallbackLanguage: Language
) : StringDesc {

    actual constructor(
        strings: Map<Language, RawStringDesc>,
        fallback: RawStringDesc
    ) : this(
        strings.plus("fallback" to fallback),
        "fallback"
    )

    actual constructor(
        strings: Map<Language, RawStringDesc>,
        fallbackCreator: LocalizedRawFallbackCreator
    ) : this(
        strings,
        fallbackCreator.create()
    )

    init {
        require(fallbackLanguage in strings.keys){
            "Missing fallback String for LocalizedRawStringDesc"
        }
    }

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        val locale = localeType.locale
        return (strings[locale.languageCode] ?: strings[fallbackLanguage]!!).localized(localeType)
    }
}