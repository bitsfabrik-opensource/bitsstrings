package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

actual data class DateTimeStringDesc actual constructor(
    val dateTime : LocalDateTime,
    val formatter: DateTimeFormatter
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: StringDesc.LocaleType): String {
        val locale = localeType.locale
        return formatter.format(dateTime, locale.toBitsLocale())
    }
}