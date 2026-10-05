package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType

actual data class CompositionStringDesc actual constructor(
    val args: Iterable<StringDesc>,
    val separator: String?
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        return args.joinToString(separator = separator ?: "") { it.localized(localeType) }
    }
}
