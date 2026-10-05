package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

/** The default apple flavour: a single pretty printed `Localizable.xcstrings` catalog. */
internal class IosStringCatalogResourceFileGeneratorTest {

    private val generator = IosStringCatalogResourceFileGenerator()

    private val file = bitsStringFile(
        section(
            "General",
            singular("general_label_ok", "de" to "OK-de", "en" to "OK-en"),
            plural(
                "general_message_count",
                "one" to mapOf("de" to "Eine Nachricht", "en" to "One message"),
                "other" to mapOf("de" to "%d Nachrichten", "en" to "%d messages"),
            ),
        ),
    )

    @Test
    fun writesASinglePrettyPrintedStringCatalog() {
        val generated = generator.generateResources(file, defaultLanguage = "de")

        assertEquals(1, generated.size)
        val (catalogFile, content) = generated.single()
        assertEquals("Localizable.xcstrings", catalogFile.path)

        // pretty printed with a two space indent, not the raw single line the transformer emits
        assertContains(content, "\n  \"sourceLanguage\": \"de\"")

        assertEquals(
            setOf("general_label_ok", "general_message_count"),
            Json.parseToJsonElement(content).jsonObject.getValue("strings").jsonObject.keys
        )
    }

    @Test
    fun keepsPluralsInTheStringCatalog() {
        val quantities = generator.catalogOf(file)
            .getValue("strings").jsonObject
            .getValue("general_message_count").jsonObject
            .getValue("localizations").jsonObject
            .getValue("de").jsonObject
            .getValue("variations").jsonObject
            .getValue("plural").jsonObject

        assertEquals(setOf("one", "other"), quantities.keys)
        assertEquals(
            "Eine Nachricht",
            quantities.getValue("one").jsonObject
                .getValue("stringUnit").jsonObject
                .getValue("value").jsonPrimitive.content
        )
    }

    @Test
    fun writesACatalogEvenForAFileWithoutStrings() {
        val generated = generator.generateResources(bitsStringFile(section("General")), "de")

        assertEquals("Localizable.xcstrings", generated.single().first.path)
    }

    private fun IosStringCatalogResourceFileGenerator.catalogOf(
        file: BitsStringFile,
        defaultLanguage: String = "de",
    ) = Json.parseToJsonElement(generateResources(file, defaultLanguage).single().second).jsonObject
}
