package com.bitsfabrik.bitsstrings.core.format

internal enum class Alignment{
    LEFT, CENTER, RIGHT;

    /**
     * How much of [padding] goes before the value.
     */
    fun paddingBefore(padding : Int) : Int = when(this){
        LEFT -> 0
        CENTER -> padding / 2
        RIGHT -> padding
    }
}