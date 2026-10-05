package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.TempDirectoryTest
import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The file writing half of the resource generation — what a [PlatformStringResourcesGenerator]
 * returns ends up on disk below the output directory. The platform flavours themselves are
 * covered by their own tests.
 */
internal class ResourceFilesGeneratorTest : TempDirectoryTest() {

    private val file = bitsStringFile(
        section("General", singular("general_label_ok", "de" to "OK-de")),
    )

    @Test
    fun writesEveryReturnedFileRelativeToTheOutputDirectory() {
        val outputDir = dir("res")
        val platformGenerator = FakePlatformStringResourcesGenerator(
            "values-de/strings.xml" to "german content",
            "values-en/strings.xml" to "english content",
        )

        ResourceFilesGenerator(outputDir, platformGenerator).generateResources(file, "de")

        assertEquals("german content", File(outputDir, "values-de/strings.xml").readText())
        assertEquals("english content", File(outputDir, "values-en/strings.xml").readText())
    }

    @Test
    fun handsTheParsedFileAndTheDefaultLanguageToThePlatformGenerator() {
        val platformGenerator = FakePlatformStringResourcesGenerator()

        ResourceFilesGenerator(dir("res"), platformGenerator).generateResources(file, "en")

        assertEquals(listOf(file to "en"), platformGenerator.calls)
    }

    @Test
    fun removesOutputOfKeysThatNoLongerExist() {
        val outputDir = dir("res")
        val stale = outputDir.writeStaleFile("values-it/strings.xml")
        val platformGenerator = FakePlatformStringResourcesGenerator(
            "values-de/strings.xml" to "german content",
        )

        ResourceFilesGenerator(outputDir, platformGenerator).generateResources(file, "de")

        assertFalse(stale.exists())
        assertTrue(File(outputDir, "values-de/strings.xml").isFile)
    }

    @Test
    fun wipesTheOutputDirectoryEvenWhenNothingIsGenerated() {
        val outputDir = dir("res")
        val stale = outputDir.writeStaleFile("values-it/strings.xml")

        ResourceFilesGenerator(outputDir, FakePlatformStringResourcesGenerator())
            .generateResources(file, "de")

        assertFalse(stale.exists())
    }

    private class FakePlatformStringResourcesGenerator(
        private vararg val files: Pair<String, String>,
    ) : PlatformStringResourcesGenerator {

        val calls = mutableListOf<Pair<BitsStringFile, String>>()

        override fun generateResources(
            bitsStringFile: BitsStringFile,
            defaultLanguage: String,
        ): List<Pair<File, String>> {
            calls += bitsStringFile to defaultLanguage
            return files.map { (path, content) -> File(path) to content }
        }
    }
}
