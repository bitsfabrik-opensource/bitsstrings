package com.bitsfabrik.bitsstrings.core.desc

@Suppress("FunctionName")
fun StringDesc.Companion.Composition(
    args: Iterable<StringDesc>,
    separator: String? = null
) = CompositionStringDesc(args, separator)

expect class CompositionStringDesc(
    args: Iterable<StringDesc>,
    separator: String? = null
) : StringDesc
