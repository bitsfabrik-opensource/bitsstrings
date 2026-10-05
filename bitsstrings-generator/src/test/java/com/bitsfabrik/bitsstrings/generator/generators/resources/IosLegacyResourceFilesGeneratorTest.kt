package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import org.gradle.api.GradleException
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The legacy apple layout: one `<lang>.lproj/Localizable.strings` per language, no plurals. */
internal class IosLegacyResourceFilesGeneratorTest {

    private val generator = IosLegacyResourceFilesGenerator()

    private val file = bitsStringFile(
        section(
            "General",
            singular("general_label_ok", "de" to "OK-de", "en" to "OK-en"),
        ),
    )

    @Test
    fun writesOneLprojDirectoryPerLanguage() {
        val generated = generator.generateResources(file, defaultLanguage = "de").toPathMap()

        assertTrue("de.lproj/Localizable.strings" in generated)
        assertTrue("en.lproj/Localizable.strings" in generated)
        assertContains(
            generated.getValue("de.lproj/Localizable.strings"),
            "\"general.label.ok\" = \"OK-de\";"
        )
        assertContains(
            generated.getValue("en.lproj/Localizable.strings"),
            "\"general.label.ok\" = \"OK-en\";"
        )
    }

    @Test
    fun doesNotWriteAStringCatalog() {
        val generated = generator.generateResources(file, defaultLanguage = "de").toPathMap()

        assertEquals(
            setOf("de.lproj/Localizable.strings", "en.lproj/Localizable.strings"),
            generated.keys
        )
    }

    @Test
    fun failsTheBuildWithAReadableMessageWhenTheFileContainsPlurals() {
        val withPlural = bitsStringFile(
            section(
                "General",
                plural("general_message_count", "one" to mapOf("de" to "Eine Nachricht")),
            ),
        )

        val error = assertFailsWith<GradleException> {
            generator.generateResources(withPlural, defaultLanguage = "de")
        }

        assertContains(error.message.orEmpty(), "ResourceFile Generation Failed!")
        assertContains(
            error.message.orEmpty(),
            "Plurals are not permitted with legacy strings generation"
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
