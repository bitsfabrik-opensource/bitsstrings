package com.bitsfabrik.bitsstrings.core.res

import com.bitsfabrik.bitsstrings.core.desc.Formatted
import com.bitsfabrik.bitsstrings.core.desc.StringDesc
import com.bitsfabrik.bitsstrings.core.desc.desc

expect class StringResource

@Suppress("SpreadOperator")
fun StringResource.format(vararg args: Any) = StringDesc.Formatted(this.desc(), *args)
fun StringResource.format(args: List<Any>) = StringDesc.Formatted(this.desc(), args)
