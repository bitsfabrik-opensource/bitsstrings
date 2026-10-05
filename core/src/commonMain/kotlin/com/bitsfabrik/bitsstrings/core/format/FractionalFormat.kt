package com.bitsfabrik.bitsstrings.core.format

import kotlin.math.abs
import kotlin.math.truncate

private const val DEFAULT_FRACTION_DIGITS = 6
private const val DECIMAL_BASE = 10
private const val ROUND_UP_FROM = 5

/**
 * Renders [value] in plain decimal notation with [fractionDigits] digits after the decimal point,
 * or with the C default of 6 digits when [fractionDigits] is `null`. Ties are rounded away from
 * zero.
 *
 * Only the integer part within the `Long` range is reproduced exactly - larger values are clamped,
 * which is the same precision limit the platform formatters have on a `Double`.
 */
internal fun fractionalFormat(value: Double, fractionDigits: Int?): String {
    if (value.isNaN()) return "NaN"
    if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

    val digits = fractionDigits ?: DEFAULT_FRACTION_DIGITS
    val magnitude = abs(value)
    val integerPart = truncate(magnitude)

    // all digits are collected without the decimal point, so that rounding can carry over it
    val allDigits = StringBuilder(integerPart.toLong().toString())
    var rest = magnitude - integerPart
    repeat(digits) {
        rest *= DECIMAL_BASE
        val digit = rest.toInt()
        allDigits.append(digit)
        rest -= digit
    }
    if (rest * DECIMAL_BASE >= ROUND_UP_FROM) allDigits.roundUp()

    val pointAt = allDigits.length - digits
    val text = if (digits == 0) {
        allDigits.toString()
    } else {
        "${allDigits.substring(0, pointAt)}.${allDigits.substring(pointAt)}"
    }
    return if (value < 0) "-$text" else text
}

/** Adds one to the decimal number held in this builder, growing it on overflow (999 -> 1000). */
private fun StringBuilder.roundUp() {
    for (i in lastIndex downTo 0) {
        if (this[i] != '9') {
            this[i] = this[i] + 1
            return
        }
        this[i] = '0'
    }
    insert(0, '1')
}
