package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.BitsLocale
import com.bitsfabrik.bitsstrings.core.res.PluralsResource
import com.bitsfabrik.bitsstrings.core.res.StringResource


fun String.desc() = StringDesc.Raw(this)
fun StringResource.desc() = StringDesc.Resource(this)
fun PluralsResource.desc(number: Int) = StringDesc.Plural(this, number)

operator fun StringDesc.plus(other: StringDesc): StringDesc {
    return StringDesc.Composition(listOf(this, other))
}

fun Iterable<StringDesc>.joinToStringDesc(separator: String = ", "): StringDesc =
    StringDesc.Composition(this, separator)


fun StringDesc.format(vararg args : Any?) : StringDesc {
    return StringDesc.Formatted(this, *args)
}

expect interface StringDesc {

    sealed class LocaleType {
        object System : LocaleType
        class Custom(@Suppress("UnusedPrivateMember") locale: BitsLocale) : LocaleType
    }

    // KEEP THIS!!
    // needed for static extension function on StringDesc to work
    companion object {

    }
}
