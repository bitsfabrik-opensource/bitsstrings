package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key

/** Maps an ini string key onto the key convention of a platform resource file. */
internal interface KeyFormatter {
    fun formatKey(key: String): String
}

