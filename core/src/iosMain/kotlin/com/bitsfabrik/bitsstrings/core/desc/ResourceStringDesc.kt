package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.res.StringResource
import com.bitsfabrik.bitsstrings.core.utils.Utils

actual data class ResourceStringDesc actual constructor(
    val stringRes: StringResource
) : StringDesc {
    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        return Utils.localizedString(stringRes.resourceId, localeType)
    }
}