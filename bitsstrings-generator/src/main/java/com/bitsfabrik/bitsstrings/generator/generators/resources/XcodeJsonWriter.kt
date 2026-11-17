package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.ibm.icu.text.Collator
import com.ibm.icu.text.RuleBasedCollator
import com.ibm.icu.util.ULocale
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializes json exactly the way Xcode saves an `.xcstrings` file, so that opening and saving a
 * generated catalog in Xcode does not change a single byte:
 * - two space indent, `"key" : value` separators
 * - object keys sorted on every level the way Xcode does, see [keyOrder]
 * - empty objects as `{`, a blank line and `}`
 * - non-ascii characters written as they are, not as `\uXXXX`
 * - no line break at the end of the file
 */
internal object XcodeJsonWriter {

    private const val INDENT = "  "

    /**
     * The order Xcode sorts the keys of every object in: Apple's Finder like `localizedStandardCompare`,
     * which is ICU collation with numbers compared by value. So `2` comes before `10`, punctuation
     * before digits before letters (`_` before `-` before `.`), accents and case only decide between
     * otherwise equal keys, lowercase first (`ab`, `aB`, `AB`). The root locale keeps the order
     * independent of the machine the build runs on.
     */
    internal val keyOrder: Comparator<String> =
        (Collator.getInstance(ULocale.ROOT) as RuleBasedCollator)
            .apply { numericCollation = true }
            .freeze()
            .let { collator -> Comparator { a: String, b: String -> collator.compare(a, b) } }

    fun write(element: JsonElement): String = buildString { append(element, depth = 0) }

    private fun StringBuilder.append(element: JsonElement, depth: Int) {
        when (element) {
            is JsonObject -> appendObject(element, depth)
            is JsonArray -> appendArray(element, depth)
            is JsonNull -> append("null")
            is JsonPrimitive ->
                if (element.isString) appendString(element.content) else append(element.content)
        }
    }

    private fun StringBuilder.appendObject(obj: JsonObject, depth: Int) {
        if (obj.isEmpty()) {
            append("{\n\n").appendIndent(depth).append("}")
            return
        }

        append("{\n")
        obj.keys.sortedWith(keyOrder).forEachIndexed { index, key ->
            if (index > 0) append(",\n")
            appendIndent(depth + 1)
            appendString(key)
            append(" : ")
            append(obj.getValue(key), depth + 1)
        }
        append("\n").appendIndent(depth).append("}")
    }

    private fun StringBuilder.appendArray(array: JsonArray, depth: Int) {
        if (array.isEmpty()) {
            append("[\n\n").appendIndent(depth).append("]")
            return
        }

        append("[\n")
        array.forEachIndexed { index, item ->
            if (index > 0) append(",\n")
            appendIndent(depth + 1)
            append(item, depth + 1)
        }
        append("\n").appendIndent(depth).append("]")
    }

    private fun StringBuilder.appendString(value: String) {
        append('"')
        value.forEach { char ->
            when (char) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                else ->
                    if (char < ' ') append("\\u%04x".format(char.code)) else append(char)
            }
        }
        append('"')
    }

    private fun StringBuilder.appendIndent(depth: Int): StringBuilder = apply {
        repeat(depth) { append(INDENT) }
    }
}
