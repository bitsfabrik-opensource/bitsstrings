package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.res.PluralsResource

@Suppress("FunctionName")
fun StringDesc.Companion.Plural(
    pluralsRes: PluralsResource,
    number: Int
) = PluralStringDesc(pluralsRes, number)

expect class PluralStringDesc(pluralsRes: PluralsResource, number: Int) : StringDesc
