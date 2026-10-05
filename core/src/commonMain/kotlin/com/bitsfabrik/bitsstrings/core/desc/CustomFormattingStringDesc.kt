package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale

@Suppress("FunctionName")
fun <T> StringDesc.Companion.Custom(obj: T, formatter: CustomFormatter<T>) = CustomFormattingStringDesc(obj, formatter)

expect class CustomFormattingStringDesc<T>(obj: T, formatter: CustomFormatter<T>) : StringDesc


fun interface CustomFormatter<T>{
    fun format(obj: T, locale : BitsLocale) : String
}
