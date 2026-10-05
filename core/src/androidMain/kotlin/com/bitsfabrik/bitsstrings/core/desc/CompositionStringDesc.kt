package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType

actual data class CompositionStringDesc actual constructor(
    val args: Iterable<StringDesc>,
    val separator: String?
) : StringDesc {
    override fun toString(context: Context, localeType : LocaleType) =
        args.joinToString(separator = separator ?: "") { it.toString(context, localeType) }
}
