package com.bitsfabrik.bitsstrings.core.format

/**
 * Reduced C-style 'sprintf' for text, integer and floating point arguments.
 *
 * A specifier looks like '%[argument$][flags][width][.precision]conversion':
 *
 * - 'argument$':  1-based index of the argument to format (e.g. '%1$s'). Just like Android and
 *                 iOS an explicitly indexed specifier does not consume an argument of its own, so
 *                 the specifier without an index keep their natural order.
 * - flags:        '-' -> left aligned
 *                 '^' -> centered
 *                 '+' -> always wirtes the sign of a positive number
 *                 '0', '*', '#', '_', '=' -> pad with that character instead of a space.
 * - width:        minimum field width (the value is padded to it)
 * - '.precision': number of fraction digits for '%f' (6 by default),
 *                 maximum length for '%s' or '%@'.
 * - conversion:   '%s', '%@': text (any argument, via 'toString()')
 *                 '%d', '%i': integer
 *                 '%f', '%F': float
 *
 * Hint: '%%' writes a literal '%'
 *
 * @throws IllegalArgumentException if the format is malformed, an argument is missing or an
 * argument does not fit the conversion it is used with.
 */
internal class Sprintf(private val format : String, private val args : Array<out Any?>) {
    private val result = StringBuilder()
    private var curPos = 0
    private var lastArgument = 0

    fun process() : String {
        while(curPos < format.length){
            val char = format[curPos++]
            when{
                // add any other char
                char != '%' -> result.append(char)

                // current char is %. add % when next is also %
                consumeIfNextChar('%') -> result.append('%')

                // current char is % found at curPos before increasing it with curPos++
                // check if there is more string left for FormatSpecifier
                curPos == format.length -> invalidFormat("unterminated format specifier")

                // parse format specifier
                else -> appendArgument(FormatSpecifier.parse(this))
            }
        }
        return result.toString()
    }

    private fun appendArgument(specifier : FormatSpecifier) {
        val index = specifier.argumentIndex ?: ++lastArgument
        if(index !in 1 .. args.size) {
            invalidFormat("there is no argument #$index")
        }
        val value = args[index -1]
        result.append(specifier.format(value){ invalidFormat("argument #$index $it")})
    }

    fun invalidFormat(reason: String) : Nothing{
        throw IllegalArgumentException("bad format: $reason at offset ${curPos -1} of \"$format\"")
    }

    fun peekChar() : Char {
        return format.getOrNull(curPos)
            ?: invalidFormat("unexpected end of format specifier")
    }

    fun nextChar() : Char {
        return peekChar().also { curPos++ }
    }

    fun consumeIfNextChar(matcher : Char) : Boolean {
        val matches = format.getOrNull(curPos) == matcher
        if(matches) {
            curPos++
        }
        return matches
    }
}


/**
 * Syntectic sugar for sprintf
 */
fun String.sprintf(vararg args : Any?) : String = Sprintf(this, args).process()