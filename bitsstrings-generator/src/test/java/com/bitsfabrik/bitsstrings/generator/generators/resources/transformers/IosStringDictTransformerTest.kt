package com.bitsfabrik.bitsstrings.generator.generators.resources.transformers

import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The transformer emits raw json text that `ResourceFilesGenerator` re-parses, so every test here
 * parses the output — if it is not valid json the real generation would fail too.
 */
class IosStringDictTransformerTest {

    private val transformer = IosStringDictTransformer()

    @Test
    fun emitsAValidCatalogWithTheDefaultLanguageAsSourceLanguage() {
        val file = bitsStringFile(
            section("General", singular("general_label_ok", "de" to "OK", "en" to "Yes")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals("de", catalog.string("sourceLanguage"))
        assertEquals("1.0", catalog.string("version"))
        assertEquals(
            "manual",
            catalog.string("strings", "general_label_ok", "extractionState")
        )
    }

    @Test
    fun keepsTheRawIniKeyRatherThanTheDottedLegacyKey() {
        val file = bitsStringFile(
            section("General", singular("login_label_PasswordReset", "de" to "Reset")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(
            setOf("login_label_PasswordReset"),
            catalog.obj("strings").keys
        )
    }

    @Test
    fun writesOneTranslatedStringUnitPerLanguageOfASingular() {
        val file = bitsStringFile(
            section("General", singular("general_label_ok", "de" to "OK", "en" to "Yes")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))
        val localizations = catalog.obj("strings", "general_label_ok", "localizations")

        assertEquals(setOf("de", "en"), localizations.keys)
        assertEquals(
            "translated",
            catalog.string("strings", "general_label_ok", "localizations", "de", "stringUnit", "state")
        )
        assertEquals(
            "OK",
            catalog.string("strings", "general_label_ok", "localizations", "de", "stringUnit", "value")
        )
        assertEquals(
            "Yes",
            catalog.string("strings", "general_label_ok", "localizations", "en", "stringUnit", "value")
        )
    }

    @Test
    fun nestsPluralQuantitiesUnderVariations() {
        val file = bitsStringFile(
            section(
                "General",
                plural(
                    "general_message_count",
                    "one" to mapOf("de" to "Eine Nachricht"),
                    "other" to mapOf("de" to "%d Nachrichten"),
                ),
            ),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))
        val plural = catalog.obj(
            "strings", "general_message_count", "localizations", "de", "variations", "plural"
        )

        assertEquals(setOf("one", "other"), plural.keys)
        assertEquals(
            "Eine Nachricht",
            catalog.string(
                "strings", "general_message_count", "localizations", "de",
                "variations", "plural", "one", "stringUnit", "value"
            )
        )
    }

    @Test
    fun skipsLanguagesAPluralWasNotTranslatedInto() {
        val file = bitsStringFile(
            section(
                "General",
                // 'en' exists in the file, but only for the singular
                singular("general_label_ok", "de" to "OK", "en" to "Yes"),
                plural("general_message_count", "one" to mapOf("de" to "Eine Nachricht")),
            ),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(
            setOf("de"),
            catalog.obj("strings", "general_message_count", "localizations").keys
        )
    }

    @Test
    fun omitsIndividualQuantitiesThatLackTheLanguage() {
        val file = bitsStringFile(
            section(
                "General",
                plural(
                    "general_message_count",
                    "one" to mapOf("de" to "Eine", "en" to "One"),
                    "other" to mapOf("de" to "Viele"),
                ),
            ),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(
            setOf("one", "other"),
            catalog.obj(
                "strings", "general_message_count", "localizations", "de", "variations", "plural"
            ).keys
        )
        assertEquals(
            setOf("one"),
            catalog.obj(
                "strings", "general_message_count", "localizations", "en", "variations", "plural"
            ).keys
        )
    }

    @Test
    fun convertsPlaceholdersToTheAppleConvention() {
        val file = bitsStringFile(
            section("General", singular("general_label_greeting", "de" to "Hallo %s und %@ (%d)")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(
            "Hallo %1\$@ und %2\$@ (%3\$d)",
            catalog.string(
                "strings", "general_label_greeting", "localizations", "de", "stringUnit", "value"
            )
        )
    }

    @Test
    fun escapesNestedQuotesSoTheCatalogStaysParseable() {
        val file = bitsStringFile(
            section("General", singular("general_label_quoted", "de" to "sag \"hallo\"")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(
            "sag \"hallo\"",
            catalog.string(
                "strings", "general_label_quoted", "localizations", "de", "stringUnit", "value"
            )
        )
    }

    @Test
    fun emitsAnEmptyButValidCatalogForAFileWithoutStrings() {
        val catalog = parse(
            transformer.processBitString(bitsStringFile(section("General")), defaultLanguage = "de")
        )

        assertTrue(catalog.obj("strings").isEmpty())
        assertEquals("de", catalog.string("sourceLanguage"))
    }

    @Test
    fun coversEverySectionOfTheFile() {
        val file = bitsStringFile(
            section("General", singular("general_label_ok", "de" to "OK")),
            section("Login", singular("login_label_reset", "de" to "Reset")),
        )

        val catalog = parse(transformer.processBitString(file, defaultLanguage = "de"))

        assertEquals(setOf("general_label_ok", "login_label_reset"), catalog.obj("strings").keys)
        assertFalse(catalog.obj("strings").containsKey("Login"))
    }

    private fun parse(raw: String): JsonObject = Json.parseToJsonElement(raw).jsonObject

    private fun JsonObject.obj(vararg path: String): JsonObject =
        path.fold(this as JsonElement) { current, key -> current.jsonObject.getValue(key) }.jsonObject

    private fun JsonObject.string(vararg path: String): String =
        path.fold(this as JsonElement) { current, key -> current.jsonObject.getValue(key) }
            .jsonPrimitive.content
}
