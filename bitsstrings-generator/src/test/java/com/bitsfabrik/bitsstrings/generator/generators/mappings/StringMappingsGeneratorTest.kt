package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.testing.TempDirectoryTest
import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Covers key mapping and file placement; the emitted kotlin itself is [PlatformStringMappingsGeneratorTest]. */
internal class StringMappingsGeneratorTest : TempDirectoryTest() {

    private val file = bitsStringFile(
        section(
            "Login",
            singular("login_label_passwordReset", "de" to "Reset"),
            plural("login_message_attemptsLeft", "one" to mapOf("de" to "Ein Versuch")),
        ),
    )

    @Test
    fun writesTheResourcesObjectIntoItsPackageDirectory() {
        val outputDir = dir("src")

        generator(outputDir, AppleStringMappingsGenerator()).generateMappings(file)

        assertTrue(File(outputDir, "com/example/app/BitsStrings.kt").isFile)
    }

    @Test
    fun namesTheFileAndTheObjectAfterTheConfiguredClassName() {
        val outputDir = dir("src")

        generator(outputDir, AppleStringMappingsGenerator(), resourcesClassName = "MR")
            .generateMappings(file)

        val generated = File(outputDir, "com/example/app/MR.kt")
        assertTrue(generated.isFile)
        assertContains(generated.readText(), "actual object MR")
    }

    @Test
    fun declaresPropertiesUnderTheirRawIniKey() {
        val code = generateAndRead(AppleStringMappingsGenerator())

        assertContains(code, "val login_label_passwordReset: StringResource")
        assertContains(code, "val login_message_attemptsLeft: PluralsResource")
    }

    @Test
    fun mapsAppleResourceIdsToTheDottedKeyOnlyInLegacyMode() {
        val legacyCode = generateAndRead(AppleStringMappingsGenerator(), legacy = true)
        val catalogCode = generateAndRead(AppleStringMappingsGenerator(), legacy = false)

        assertContains(legacyCode, "StringResource(resourceId = \"login.label.passwordReset\")")
        assertContains(catalogCode, "StringResource(resourceId = \"login_label_passwordReset\")")
    }

    @Test
    fun alwaysSnakeCasesAndroidResourceNames() {
        val legacyCode = generateAndRead(androidGenerator(), legacy = true)
        val catalogCode = generateAndRead(androidGenerator(), legacy = false)

        // the legacy flag only affects apple keys
        assertContains(legacyCode, "StringResource(R.string.login_label_password_reset)")
        assertContains(catalogCode, "StringResource(R.string.login_label_password_reset)")
        assertContains(catalogCode, "PluralsResource(R.plurals.login_message_attempts_left)")
    }

    @Test
    fun collectsKeysFromEverySectionInOrder() {
        val multiSection = bitsStringFile(
            section("General", singular("general_label_ok", "de" to "OK")),
            section("Login", singular("login_label_reset", "de" to "Reset")),
        )
        val outputDir = dir("src")

        generator(outputDir, AppleStringMappingsGenerator()).generateMappings(multiSection)

        val code = File(outputDir, "com/example/app/BitsStrings.kt").readText()
        assertTrue(code.indexOf("general_label_ok") < code.indexOf("login_label_reset"), code)
    }

    @Test
    fun wipesPreviouslyGeneratedSourcesBeforeWriting() {
        val outputDir = dir("src")
        val stale = outputDir.writeStaleFile("com/example/app/RemovedStrings.kt")

        generator(outputDir, AppleStringMappingsGenerator()).generateMappings(file)

        assertFalse(stale.exists())
        assertTrue(File(outputDir, "com/example/app/BitsStrings.kt").isFile)
    }

    @Test
    fun writesAnObjectWithoutPropertiesForAFileWithoutKeys() {
        val outputDir = dir("src")

        generator(outputDir, AppleStringMappingsGenerator())
            .generateMappings(bitsStringFile(section("General")))

        val code = File(outputDir, "com/example/app/BitsStrings.kt").readText()
        assertContains(code, "actual object BitsStrings")
        assertFalse(code.contains("val "), code)
    }

    @Test
    fun putsTheObjectInTheConfiguredPackage() {
        val outputDir = dir("src")

        generator(outputDir, AppleStringMappingsGenerator(), resourcesPackage = "org.other.app")
            .generateMappings(file)

        val generated = File(outputDir, "org/other/app/BitsStrings.kt")
        assertTrue(generated.isFile)
        assertContains(generated.readText(), "package org.other.app")
    }

    private fun androidGenerator() =
        AndroidStringMappingsGenerator("com.example.app", isActual = true)

    private fun generateAndRead(
        platformGenerator: PlatformStringMappingsGenerator,
        legacy: Boolean = false,
    ): String {
        val outputDir = dir("src-${platformGenerator::class.simpleName}-$legacy")
        generator(outputDir, platformGenerator, legacy = legacy).generateMappings(file)
        return File(outputDir, "com/example/app/BitsStrings.kt").readText()
    }

    private fun generator(
        outputSourcesDir: File,
        platformGenerator: PlatformStringMappingsGenerator,
        legacy: Boolean = false,
        resourcesPackage: String = "com.example.app",
        resourcesClassName: String = "BitsStrings",
    ) = StringMappingsGenerator(
        outputSourcesDir,
        platformGenerator,
        legacy,
        resourcesPackage,
        resourcesClassName,
    )
}
