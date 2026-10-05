package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.res.PluralsResource
import com.bitsfabrik.bitsstrings.core.utils.Utils


actual data class PluralStringDesc actual constructor(
    val pluralsRes: PluralsResource,
    val number: Int
) : StringDesc {
    override fun toString(context: Context, localeType: LocaleType): String {
        return Utils.resourcesForContext(context, localeType).getQuantityString(pluralsRes.resourceId, number)
    }
}

