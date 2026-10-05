package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value

/** Rewrites a translation value from `strings.ini` into what a platform resource file accepts. */
internal interface ValueFormatter {
    fun formatValue(value: String): String
}


