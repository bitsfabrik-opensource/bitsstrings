package com.bitsfabrik.bitsstrings.core.format

/**
 * Representation of a single %[argumentIndex$][flags][width][.precision]conversion specification.
 * [argumentIndex] is a 1-based index.
 */
internal data class FormatSpecifier(
    val flags: Flags,
    val argumentIndex : Int?,
    val width : Int?,
    val precision  : Int?,
    val conversion: Conversion,
){

    fun format(value : Any?, fail : (String)->Nothing) : String{
        return when(conversion){
            Conversion.TEXT -> {
                val text = value.toString()
                field(if(precision != null) text.take(precision) else text)
            }
            Conversion.INTEGER -> {
                signedField(value.asNumber(fail).toLong().toString())
            }
            Conversion.FLOAT -> {
                signedField(fractionalFormat(value.asNumber(fail).toDouble(), precision))
            }
        }
    }

    private fun Any?.asNumber(fail : (String)->Nothing) : Number{
        return this as? Number ?: fail("is not a number")
    }

    private fun signedField(number : String) : String{
        val signed = if(flags.explicitPlus && !number.startsWith('-')) "+$number" else number
        val hasSign = signed.startsWith('+') || signed.startsWith('-')
        // a zero filled field keeps the sign in front: -0032 instead of 00-32
        return if(flags.fillChar == '0' && hasSign){
            field(signed.drop(1), signed.take(1))
        } else{
            field(signed)
        }
    }

    private fun field(text : String, prefix : String = "") : String{
        val padding = (width ?: 0) - prefix.length - text.length
        return if(padding <= 0){
            prefix + text
        } else {
            val before = flags.alignment.paddingBefore(padding)
            prefix + createFiller(before) + text + createFiller(padding - before)
        }
    }

    private fun createFiller(size : Int) : String{
        return flags.fillChar.toString().repeat(size)
    }

    companion object{
        /**
         * Reads a FormatSpecifier from the current position of [source].
         */
        fun parse(source: Sprintf) : FormatSpecifier = Parser(source).parse()
    }
}


private class Parser(private val source : Sprintf){

    fun parse() : FormatSpecifier{
        var flags = Flags()
        var argumentIndex : Int? = null
        var width : Int? = null

        // Phase 1: Read Flags/ArgumentIndex/Width
        // Flags and ArgumentIndex can be in any order. Width always ends Phase1
        do{
            flags = flags.updateFlags(source)
            val phase1Finished = readNumber()?.let {
                if(argumentIndex == null && source.consumeIfNextChar('$')){
                    argumentIndex = it
                    false
                } else {
                    width = it
                    true
                }
            } ?: true

        } while (!phase1Finished)

        // Phase 2: Read Precision
        val precision = if(source.consumeIfNextChar('.')) readNumber(true) else null

        // Phase 3: Read Conversion
        val conversion = readConversion()

        return FormatSpecifier(
            flags,
            argumentIndex,
            width,
            precision,
            conversion
        )
    }

    private fun readNumber(zeroPrefixedAllowed : Boolean = false) : Int?{
        val digits = StringBuilder()

        fun canAddNextChar() : Boolean {
            val char = source.peekChar()
            return char.isDigit() && (digits.isNotEmpty() || char != '0' || zeroPrefixedAllowed)
        }

        while(canAddNextChar()){
            digits.append(source.nextChar())
        }

        return if(digits.isEmpty()) null else digits.toString().toInt()
    }

    private fun readConversion() : Conversion{
        val char = source.nextChar()
        return Conversion.of(char) ?: source.invalidFormat("unexpected conversion '$char'")
    }
}