package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.desc.StringDesc.LocaleType
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual class NumberFormattedStringDesc actual constructor(
    val number: Number,
    val formatter: NumberFormatter
) : StringDesc {

    override fun localized(): String {
        return localized(LocaleType.System)
    }

    override fun localized(localeType: LocaleType): String {
        val locale = localeType.locale
        return formatter.format(number, locale.toBitsLocale())
    }
}

actual open class CommonNumberFormatter actual constructor(
    val minIntegerDigits: Int,
    val minFractionDigits: Int,
    val maxFractionDigits: Int,
    val useGrouping: Boolean
) : NumberFormatter {

    actual override fun format(
        number: Number,
        locale: BitsLocale
    ): String {
        val formatter = NSNumberFormatter()
        formatter.setMinimumIntegerDigits(minIntegerDigits.toULong())
        formatter.setMinimumFractionDigits(minFractionDigits.toULong())
        formatter.setMaximumFractionDigits(maxFractionDigits.toULong())
        formatter.setUsesGroupingSeparator(useGrouping)
        formatter.setLocale(locale.toPlatformLocale())
        return when (number) {
            is Int -> formatter.stringFromNumber(NSNumber(int = number))
            is Long -> formatter.stringFromNumber(NSNumber(long = number))
            is Float -> formatter.stringFromNumber(NSNumber(float = number))
            is Double -> formatter.stringFromNumber(NSNumber(double = number))
            is Short -> formatter.stringFromNumber(NSNumber(short = number))
            is Byte -> formatter.stringFromNumber(NSNumber(char = number))
            else -> null
        } ?: number.toString()
    }
}




