package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
actual data class InstantStringDesc actual constructor(
    val instant : Instant,
    val formatter: InstantFormatter
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: StringDesc.LocaleType): String {
        val locale = localeType.locale
        return formatter.format(instant, locale.toBitsLocale())
    }
}