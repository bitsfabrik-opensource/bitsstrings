package com.bitsfabrik.bitsstrings.core.format

internal enum class Conversion(private val chars : String) {
    TEXT("s@"),
    INTEGER("di"),
    FLOAT("fF");

    companion object{
        fun of(char : Char) : Conversion? = entries.firstOrNull { char in it.chars }
    }
}