package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale

@Suppress("FunctionName")
fun StringDesc.Companion.Number(
    number: Number,
    formatter: NumberFormatter
) = NumberFormattedStringDesc(number, formatter)

@Suppress("FunctionName")
fun StringDesc.Companion.Currency(
    number: Number,
) = NumberFormattedStringDesc(number, CurrencyFormatter())

fun interface NumberFormatter {
    fun format(
        number: Number,
        locale: BitsLocale
    ): String
}

expect class NumberFormattedStringDesc(
    number: Number,
    formatter: NumberFormatter
) : StringDesc

expect open class CommonNumberFormatter(
    minIntegerDigits: Int,
    minFractionDigits: Int,
    maxFractionDigits: Int,
    useGrouping: Boolean
) : NumberFormatter {
    override fun format(
        number: Number,
        locale: BitsLocale
    ): String
}

open class CurrencyFormatter : CommonNumberFormatter(
    minIntegerDigits = 1,
    minFractionDigits = 2,
    maxFractionDigits = 2,
    useGrouping = false
)




