package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.Utils
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale


actual data class CustomFormattingStringDesc<T> actual constructor(
    val obj: T,
    val formatter: CustomFormatter<T>
) : StringDesc {
    override fun toString(context: Context,  localeType: LocaleType) : String{
        val contextLocale = Utils.localeForContext(context,localeType)
        return formatter.format(obj, contextLocale.toBitsLocale())
    }
}

