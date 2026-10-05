package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.Utils
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate

actual data class DateStringDesc actual constructor(
    val date : LocalDate,
    val formatter: DateFormatter
) : StringDesc {

    override fun toString(context: Context,  localeType: LocaleType) : String{
        val contextLocale = Utils.localeForContext(context,localeType)
        return formatter.format(date, contextLocale.toBitsLocale())
    }
}