package com.bitsfabrik.bitsstrings.core.desc

import com.bitsfabrik.bitsstrings.core.Language

@Suppress("FunctionName")
fun StringDesc.Companion.LocalizedRaw(
    strings: Map<Language, RawStringDesc>,
    fallbackLanguage: Language,
    createFallbackIfMissing : Boolean = false,
    fallbackCreator : LocalizedRawFallbackCreator = LocalizedRawFallbackCreator{ RawStringDesc("") }
) = if(createFallbackIfMissing && fallbackLanguage !in strings.keys){
        LocalizedRawStringDesc(strings, fallbackCreator)
    } else {
        LocalizedRawStringDesc(strings, fallbackLanguage)
    }

@Suppress("FunctionName")
fun StringDesc.Companion.LocalizedRaw(
    strings: Map<Language, RawStringDesc>,
    fallback: RawStringDesc
) = LocalizedRawStringDesc(strings, fallback)



fun interface LocalizedRawFallbackCreator{
    fun create() : RawStringDesc
}

expect class LocalizedRawStringDesc(
    strings: Map<Language, RawStringDesc>,
    fallbackLanguage: Language
) : StringDesc {

    constructor(
        strings: Map<Language, RawStringDesc>,
        fallback: RawStringDesc
    )

    constructor(
        strings: Map<Language, RawStringDesc>,
        fallbackCreator: LocalizedRawFallbackCreator
    )
}



fun Map<Language, RawStringDesc>.prioritizedFallback(language1 : Language, vararg otherLanguage: Language) : Language{
    return this.prioritizedFallback(listOf(language1, *otherLanguage))
}

fun Map<Language, RawStringDesc>.prioritizedFallback(languages : List<Language>) : Language{
    return languages.firstOrNull { it in this.keys } ?: this.keys.first()
}

