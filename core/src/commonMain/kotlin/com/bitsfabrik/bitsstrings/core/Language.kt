package com.bitsfabrik.bitsstrings.core

typealias Language = String
typealias Country = String
typealias Variant = String


data class BitsLocale(
    val language : Language,
    val country : Country?,
    val variant : Variant?
) {
    constructor(locale : String) : this(locale.split("-"))

    private constructor(localeDescription : List<String>) : this(
        localeDescription[0],
        localeDescription.getOrNull(1),
        localeDescription.getOrNull(2),
    ){
        if(localeDescription.size > 3){
            throw IllegalArgumentException(
                "Invalid language description $localeDescription which has more than three parts."
            )
        }
    }
}



