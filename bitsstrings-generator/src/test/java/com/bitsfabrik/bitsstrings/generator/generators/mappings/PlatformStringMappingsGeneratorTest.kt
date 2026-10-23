package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.FileSpec
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Covers the kotlin each platform generator emits, independent of file writing. */
class PlatformStringMappingsGeneratorTest {

    private val singularKey = StringMetaData(
        key = "general_label_ok",
        android = "general_label_ok",
        apple = "general.label.ok",
        isPlural = false,
    )

    private val pluralKey = StringMetaData(
        key = "general_message_count",
        android = "general_message_count",
        apple = "general.message.count",
        isPlural = true,
    )

    @Test
    fun commonDeclaresAnExpectObjectWithoutInitializers() {
        val code = generate(CommonStringMappingsGenerator())

        assertContains(code, "expect object BitsStrings")
        assertContains(code, "val general_label_ok: StringResource")
        assertContains(code, "val general_message_count: PluralsResource")
        // an expect declaration must not have a body
        assertFalse(code.contains("="), "expect properties must not be initialized:\n$code")
    }

    @Test
    fun commonImportsTheResourceTypesFromCore() {
        val code = generate(CommonStringMappingsGenerator())

        assertContains(code, "import com.bitsfabrik.bitsstrings.core.res.StringResource")
        assertContains(code, "import com.bitsfabrik.bitsstrings.core.res.PluralsResource")
    }

    @Test
    fun appleResolvesThroughTheResourceIdOfItsOwnTable() {
        val code = generate(AppleStringMappingsGenerator("LoginStrings"))

        assertContains(code, "actual object BitsStrings")
        assertContains(
            code,
            "StringResource(resourceId = \"general.label.ok\", tableName = \"LoginStrings\")"
        )
        assertContains(
            code,
            "PluralsResource(resourceId = \"general.message.count\", tableName = \"LoginStrings\")"
        )
    }

    @Test
    fun appleMarksTheObjectAndEveryPropertyAsActual() {
        val code = generate(AppleStringMappingsGenerator("LoginStrings"))

        assertEquals(3, code.split("actual").size - 1, "object plus two properties:\n$code")
    }

    @Test
    fun androidResolvesThroughTheGeneratedRClass() {
        val code = generate(AndroidStringMappingsGenerator("com.example.app", isActual = true))

        assertContains(code, "import com.example.app.R")
        assertContains(code, "StringResource(R.string.general_label_ok)")
        assertContains(code, "PluralsResource(R.plurals.general_message_count)")
    }

    @Test
    fun androidUsesTheSnakeCasedResourceNameNotTheIniKey() {
        val camelCaseKey = StringMetaData(
            key = "login_label_passwordReset",
            android = "login_label_password_reset",
            apple = "login.label.passwordReset",
            isPlural = false,
        )

        val code = generate(
            AndroidStringMappingsGenerator("com.example.app", isActual = true),
            camelCaseKey
        )

        // the property keeps the ini key, only the R reference is snake cased
        assertContains(code, "val login_label_passwordReset: StringResource")
        assertContains(code, "StringResource(R.string.login_label_password_reset)")
    }

    @Test
    fun androidOmitsActualForTheStandaloneAndroidPlugin() {
        val code = generate(AndroidStringMappingsGenerator("com.example.app", isActual = false))

        assertFalse(code.contains("actual"), "android only projects have no expect to match:\n$code")
        assertContains(code, "object BitsStrings")
        assertContains(code, "StringResource(R.string.general_label_ok)")
    }

    @Test
    fun emitsAnEmptyObjectForAFileWithoutKeys() {
        val code = generate(CommonStringMappingsGenerator(), *emptyArray())

        assertContains(code, "expect object BitsStrings")
        assertFalse(code.contains("val "), code)
    }

    @Test
    fun emitsPropertiesInTheOrderTheKeysWereParsed() {
        val code = generate(AppleStringMappingsGenerator("LoginStrings"))

        assertTrue(
            code.indexOf("general_label_ok") < code.indexOf("general_message_count"),
            code
        )
    }

    private fun generate(
        generator: PlatformStringMappingsGenerator,
        vararg metadata: StringMetaData = arrayOf(singularKey, pluralKey),
    ): String {
        val fileSpec = FileSpec.builder("com.example.app", "BitsStrings")
        generator.generateClass(fileSpec, "BitsStrings", metadata.toList())
        return fileSpec.build().toString()
    }
}
