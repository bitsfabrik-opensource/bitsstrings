package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value

internal object AndroidValueFormatter : ValueFormatter {

    /**
     * Matches recognized Android-supported HTML tags (opening, closing, or self-closing),
     * e.g. <b>, </b>, <a href="...">, <br/>.
     * Used to protect known markup from XML entity escaping.
     */
    private val allowedHtmlTagRegex = Regex(
        "</?(b|i|u|font|a|p|br|big|small|sub|sup|strike|li|marquee)\\b[^<>]*/?>",
        RegexOption.IGNORE_CASE
    )

    override fun formatValue(value: String): String =
        replaceSpecialCharacters(value.withPositionalPlaceholders(stringConversion = "s"))

    /**
     * Escapes special XML characters for Android string resources, while preserving
     * recognized HTML tags (e.g. <b>, <a href="...">) so they remain valid markup
     * instead of being escaped into literal text.
     *
     * Steps:
     * 1. Extract known HTML tags and replace them with placeholders, so they are
     *    not affected by the escaping logic below.
     * 2. Escape remaining special characters (order matters — '&' must be escaped first,
     *    otherwise entities added in later steps would get double-escaped).
     * 3. Restore the original HTML tags in place of their placeholders.
     */
    private fun replaceSpecialCharacters(input: String): String {
        val placeholders = mutableListOf<String>()

        // 1. Extract known HTML tags and replace them with placeholders
        val protected = allowedHtmlTagRegex.replace(input) { match ->
            placeholders.add(match.value)
            "\u0000${placeholders.size - 1}\u0000"
        }

        // 2. Escape remaining characters (must escape '&' first!)
        val escaped = protected
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("'", "\\'")
            .replace("\\\"", "\"")

        // 3. Restore original HTML tags from their placeholders
        return placeholders.foldIndexed(escaped) { index, acc, tag ->
            acc.replace("\u0000$index\u0000", tag)
        }
    }
}