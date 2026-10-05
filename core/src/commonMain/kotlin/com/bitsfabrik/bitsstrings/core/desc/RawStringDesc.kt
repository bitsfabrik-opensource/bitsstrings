package com.bitsfabrik.bitsstrings.core.desc

@Suppress("FunctionName")
fun StringDesc.Companion.Raw(string: String) = RawStringDesc(string)

expect class RawStringDesc(string: String) : StringDesc