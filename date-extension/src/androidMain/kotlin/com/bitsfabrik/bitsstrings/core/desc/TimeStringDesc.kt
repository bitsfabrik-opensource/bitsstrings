package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.Utils
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

actual data class TimeStringDesc actual constructor(
    val time : LocalTime,
    val formatter: TimeFormatter
) : StringDesc {

    override fun toString(context: Context,  localeType: LocaleType) : String{
        val contextLocale = Utils.localeForContext(context,localeType)
        return formatter.format(time, contextLocale.toBitsLocale())
    }
}