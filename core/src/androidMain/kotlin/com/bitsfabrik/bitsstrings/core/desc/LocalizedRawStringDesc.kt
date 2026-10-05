package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import android.os.Build
import com.bitsfabrik.bitsstrings.core.Language
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.Utils


actual class LocalizedRawStringDesc actual constructor(
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

    override fun toString(context: Context, localeType: LocaleType): String {
        val contextLocale = Utils.localeForContext(context,localeType)
        return (strings[contextLocale.language] ?: strings[fallbackLanguage]!!).toString(context, localeType)
    }
}