package com.bitsfabrik.bitsstrings.core.format

internal data class Flags(
    val alignment: Alignment = Alignment.RIGHT,
    val explicitPlus : Boolean = false,
    val fillChar : Char = ' '
){
    fun updateFlags(source : Sprintf) : Flags{
        var temp = this
        while (source.peekChar() in FLAG_CHARS){
            when(val ch = source.nextChar()){
                LEFT_ALIGNMENT_CHAR -> temp = temp.copy(alignment = Alignment.LEFT)
                CENTER_ALIGNMENT_CHAR -> temp = temp.copy(alignment = Alignment.CENTER)
                SIGN_CHAR -> temp = temp.copy(explicitPlus = true)
                in FILL_CHARS -> temp = temp.copy(fillChar = ch)
            }
        }
        return temp
    }

    companion object{
        private const val FILL_CHARS = "0*#_="

        private const val LEFT_ALIGNMENT_CHAR = '-'
        private const val CENTER_ALIGNMENT_CHAR = '^'
        private const val ALIGNMENT_CHARS = "$LEFT_ALIGNMENT_CHAR$CENTER_ALIGNMENT_CHAR"

        private const val SIGN_CHAR = '+'

        private const val FLAG_CHARS = ALIGNMENT_CHARS + FILL_CHARS + SIGN_CHAR
    }
}
