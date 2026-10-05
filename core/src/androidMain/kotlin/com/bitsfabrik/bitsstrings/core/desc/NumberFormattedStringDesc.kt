package com.bitsfabrik.bitsstrings.core.desc

import android.content.Context
import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.utils.Utils
import com.bitsfabrik.bitsstrings.core.utils.toBitsLocale
import com.bitsfabrik.bitsstrings.core.utils.toPlatformLocale
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

actual class NumberFormattedStringDesc actual constructor(
    val number: Number,
    val formatter: NumberFormatter
) : StringDesc {

    override fun toString(
        context: Context,
        localeType: StringDesc.LocaleType
    ): String {
        val contextLocale = Utils.localeForContext(context, localeType)
        return formatter.format(number, contextLocale.toBitsLocale())
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
        return DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols(locale.toPlatformLocale())
            maximumFractionDigits = maxFractionDigits
            minimumFractionDigits = minFractionDigits
            minimumIntegerDigits = minIntegerDigits
            isGroupingUsed = useGrouping
        }.format(number)
    }
}




