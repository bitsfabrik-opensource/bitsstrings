package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value

internal object IosValueFormatter : ValueFormatter {

    /** A quoted section inside the value — apple resource formats need those quotes escaped. */
    private val nestedQuotingRegex = "\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()

    override fun formatValue(value: String): String =
        replaceSpecialCharacters(value.withPositionalPlaceholders(stringConversion = "@"))

    private fun replaceSpecialCharacters(input: String): String =
        nestedQuotingRegex.replace(input) { match ->
            "\\\"" + match.groupValues[1] + "\\\""
        }
}