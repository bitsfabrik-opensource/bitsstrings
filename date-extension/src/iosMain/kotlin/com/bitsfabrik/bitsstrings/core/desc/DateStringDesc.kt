package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate

actual data class DateStringDesc actual constructor(
    val date : LocalDate,
    val formatter: DateFormatter
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: StringDesc.LocaleType): String {
        val locale = localeType.locale
        return formatter.format(date, locale.toBitsLocale())
    }
}