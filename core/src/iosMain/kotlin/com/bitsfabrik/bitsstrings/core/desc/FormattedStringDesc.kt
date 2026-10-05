package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.format.sprintf
import com.bitsfabrik.bitsstrings.core.res.StringResource
import com.bitsfabrik.bitsstrings.core.utils.Utils

actual data class FormattedStringDesc actual constructor(
    val format: StringDesc,
    val args: List<Any?>
) : StringDesc {
    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        return format.localized(localeType).sprintf(*Utils.processArgs(args, localeType))
    }
}