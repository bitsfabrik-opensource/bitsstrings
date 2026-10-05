package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

actual data class TimeStringDesc actual constructor(
    val time : LocalTime,
    val formatter: TimeFormatter
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: StringDesc.LocaleType): String {
        val locale = localeType.locale
        return formatter.format(time, locale.toBitsLocale())
    }
}