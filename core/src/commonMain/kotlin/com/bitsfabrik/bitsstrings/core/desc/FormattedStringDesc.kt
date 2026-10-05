package com.bitsfabrik.bitsstrings.core.desc


@Suppress("FunctionName")
fun StringDesc.Companion.Formatted(
    stringRes: StringDesc,
    args: List<Any?>
) = FormattedStringDesc(stringRes, args)

@Suppress("FunctionName")
fun StringDesc.Companion.Formatted(
    stringRes: StringDesc,
    vararg args: Any?
) = FormattedStringDesc(stringRes, args.asList())


expect class FormattedStringDesc(format: StringDesc, args: List<Any?>) : StringDesc