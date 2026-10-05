package com.bitsfabrik.bitsstrings.generator.testing

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringSection

/**
 * Builders for the parsed-ini model. Every map is insertion ordered, so the language and quantity
 * order a test declares is the order the generators emit.
 */

internal fun bitsStringFile(vararg sections: BitsStringSection) = BitsStringFile(sections.toList())

internal fun section(title: String, vararg definitions: BitsStringDefinition) =
    BitsStringSection(title, definitions.toList())

/** `singular("general_label_ok", "de" to "OK", "en" to "Yes")` */
internal fun singular(key: String, vararg translations: Pair<String, String>) =
    BitsStringDefinition.Singular(key, linkedMapOf(*translations))

/** `plural("count", "one" to mapOf("de" to "eine"), "other" to mapOf("de" to "%d"))` */
internal fun plural(key: String, vararg quantities: Pair<String, Map<String, String>>) =
    BitsStringDefinition.Plural(key, linkedMapOf(*quantities))
