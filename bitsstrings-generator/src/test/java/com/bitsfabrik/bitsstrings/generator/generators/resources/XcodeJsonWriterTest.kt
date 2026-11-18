package com.bitsfabrik.bitsstrings.generator.generators.resources

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

internal class XcodeJsonWriterTest {

    /**
     * `XcodeFormatted.xcstrings` was saved by Xcode itself, with keys and values chosen to cover the
     * sorting and escaping edge cases. Writing it back must not change a single byte.
     */
    @Test
    fun writesACatalogSavedByXcodeByteIdentical() {
        val saved = javaClass.getResource("/xcstrings/XcodeFormatted.xcstrings")!!.readText()

        assertEquals(saved, XcodeJsonWriter.write(Json.parseToJsonElement(saved)))
    }

    @Test
    fun indentsByTwoSpacesWithSpacedSeparatorsAndNoTrailingLineBreak() {
        val json = buildJsonObject {
            put("version", "1.0")
            putJsonObject("strings") { putJsonObject("ok") { put("state", "translated") } }
        }

        assertEquals(
            """
            {
              "strings" : {
                "ok" : {
                  "state" : "translated"
                }
              },
              "version" : "1.0"
            }
            """.trimIndent(),
            XcodeJsonWriter.write(json)
        )
    }

    @Test
    fun writesEmptyObjectsWithABlankLine() {
        val json = buildJsonObject { putJsonObject("strings") { putJsonObject("empty") {} } }

        assertEquals(
            "{\n  \"strings\" : {\n    \"empty\" : {\n\n    }\n  }\n}",
            XcodeJsonWriter.write(json)
        )
    }

    @Test
    fun sortsKeysLikeXcode() {
        val xcodeOrder = listOf(
            "_leading", "2", "10", "a_b", "a-b", "a.b", "A1", "a9", "a10",
            "ab", "aB", "AB", "ärger", "b_key", "general_label", "generalLabel", "z", "Z_upper",
        )

        assertEquals(xcodeOrder, xcodeOrder.reversed().sortedWith(XcodeJsonWriter.keyOrder))
    }

    @Test
    fun keepsUnicodeButEscapesQuotesBackslashesAndControlCharacters() {
        val json = buildJsonObject { put("value", "Grüße 😀 \"x\" \\ eins\nzwei\ttab") }

        assertEquals(
            "{\n  \"value\" : \"Grüße 😀 \\\"x\\\" \\\\ eins\\nzwei\\ttab\"\n}",
            XcodeJsonWriter.write(json)
        )
    }
}
