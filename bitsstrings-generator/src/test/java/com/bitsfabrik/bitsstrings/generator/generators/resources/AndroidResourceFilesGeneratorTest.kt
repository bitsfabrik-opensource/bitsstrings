package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The android `res` layout: one `values-<lang>` per language plus the unqualified fallback. */
internal class AndroidResourceFilesGeneratorTest {

    private val generator = AndroidResourceFilesGenerator()

    private val file = bitsStringFile(
        section(
            "General",
            singular("general_label_ok", "de" to "OK-de", "en" to "OK-en"),
            plural(
                "general_message_count",
                "one" to mapOf("de" to "Eine Nachricht"),
                "other" to mapOf("de" to "%d Nachrichten"),
            ),
        ),
    )

    @Test
    fun writesOneValuesDirectoryPerLanguage() {
        val generated = generator.generateResources(file, defaultLanguage = "de").toPathMap()

        assertTrue("values-de/strings.xml" in generated)
        assertTrue("values-en/strings.xml" in generated)
        assertContains(
            generated.getValue("values-de/strings.xml"),
            "<string name=\"general_label_ok\">OK-de</string>"
        )
        assertContains(
            generated.getValue("values-en/strings.xml"),
            "<string name=\"general_label_ok\">OK-en</string>"
        )
    }

    @Test
    fun copiesTheDefaultLanguageIntoTheUnqualifiedValuesDirectory() {
        val generated = generator.generateResources(file, defaultLanguage = "de").toPathMap()

        assertEquals(
            generated.getValue("values-de/strings.xml"),
            generated.getValue("values/strings.xml")
        )
    }

    @Test
    fun doesNotWriteAFallbackWhenNoLanguageMatchesTheDefault() {
        //TODO think about this usecase. do we like to always have a default string file?
        val generated = generator.generateResources(file, defaultLanguage = "fr").toPathMap()

        assertFalse("values/strings.xml" in generated)
        assertTrue("values-de/strings.xml" in generated)
    }

    @Test
    fun supportsPlurals() {
        val generated = generator.generateResources(file, defaultLanguage = "de").toPathMap()

        assertContains(
            generated.getValue("values-de/strings.xml"),
            "\t<plurals name=\"general_message_count\">\n" +
                    "\t\t<item quantity=\"one\">Eine Nachricht</item>\n" +
                    "\t\t<item quantity=\"other\">%1\$d Nachrichten</item>\n" +
                    "\t</plurals>\n"
        )
    }

    @Test
    fun generatesNothingForAFileWithoutStrings() {
        val generated = generator.generateResources(bitsStringFile(section("General")), "de")

        assertEquals(emptyList(), generated)
    }

    private fun List<Pair<File, String>>.toPathMap(): Map<String, String> =
        associate { (file, content) -> file.path to content }
}
