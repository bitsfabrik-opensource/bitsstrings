package com.bitsfabrik.bitsstrings.core.res

import com.bitsfabrik.bitsstrings.core.desc.Formatted
import com.bitsfabrik.bitsstrings.core.desc.StringDesc
import com.bitsfabrik.bitsstrings.core.desc.desc

expect class PluralsResource

fun PluralsResource.format(number: Int, vararg args: Any) =
    StringDesc.Formatted(this.desc(number), number, args.asList())

fun PluralsResource.format(number: Int, args: List<Any>) =
    StringDesc.Formatted(this.desc(number), number, args)
