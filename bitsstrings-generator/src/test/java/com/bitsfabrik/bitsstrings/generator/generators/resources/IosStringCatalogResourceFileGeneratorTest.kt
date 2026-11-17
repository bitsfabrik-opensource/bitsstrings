package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** The default apple flavour: a single pretty printed `<table>.xcstrings` catalog. */
internal class IosStringCatalogResourceFileGeneratorTest {

    private val generator = IosStringCatalogResourceFileGenerator("LoginStrings")

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
        assertEquals("LoginStrings.xcstrings", catalogFile.path)

        // written the way Xcode saves it: two space indent, " : " separators, no trailing line break
        assertContains(content, "\n  \"sourceLanguage\" : \"de\",\n")
        assertFalse(content.endsWith("\n"))

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

        assertEquals("LoginStrings.xcstrings", generated.single().first.path)
    }

    @Test
    fun claimsOnlyItsOwnCatalogInTheSharedResourcesDirectory() {
        val outputDir = File("resources/bitsstrings")

        assertEquals(
            listOf(File(outputDir, "LoginStrings.xcstrings")),
            generator.ownedFiles(outputDir)
        )
    }

    private fun IosStringCatalogResourceFileGenerator.catalogOf(
        file: BitsStringFile,
        defaultLanguage: String = "de",
    ) = Json.parseToJsonElement(generateResources(file, defaultLanguage).single().second).jsonObject
}
