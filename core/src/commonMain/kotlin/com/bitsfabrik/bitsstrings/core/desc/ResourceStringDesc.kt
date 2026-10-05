package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.res.StringResource

@Suppress("FunctionName")
fun StringDesc.Companion.Resource(stringRes: StringResource) = ResourceStringDesc(stringRes)

expect class ResourceStringDesc(stringRes: StringResource) : StringDesc
