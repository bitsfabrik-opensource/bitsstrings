package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.res.StringResource
import com.bitsfabrik.bitsstrings.core.utils.Utils

actual data class ResourceStringDesc actual constructor(
    val stringRes: StringResource
) : StringDesc {
    override fun toString(context: Context, localeType: LocaleType): String {
        return Utils.resourcesForContext(context, localeType).getString(stringRes.resourceId)
    }
}
