package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.format.sprintf
import com.bitsfabrik.bitsstrings.core.utils.Utils

actual data class FormattedStringDesc actual constructor(
    val format: StringDesc,
    val args: List<Any?>
) : StringDesc {

    override fun toString(context: Context, localeType: LocaleType): String {
        return format.toString(context, localeType).sprintf(
            *Utils.processArgs(args, context, localeType)
        )
    }
}

