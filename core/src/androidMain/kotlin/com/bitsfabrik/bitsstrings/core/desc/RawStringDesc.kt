package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType

actual data class RawStringDesc actual constructor(val string: String) : StringDesc {
    override fun toString(context: Context,  localeType: LocaleType) = string
}

