package com.bitsfabrik.bitsstrings.generator.domain

import com.bitsfabrik.bitsstrings.generator.testing.bitsStringFile
import com.bitsfabrik.bitsstrings.generator.testing.plural
import com.bitsfabrik.bitsstrings.generator.testing.section
import com.bitsfabrik.bitsstrings.generator.testing.singular
import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

internal class KeyPrefixValidationTest {

    private val file = bitsStringFile(
        section(
            "Login",
            singular("login_label_ok", "de" to "OK"),
            plural("login_message_count", "one" to mapOf("de" to "Eine")),
        ),
        section(
            "General",
            singular("general_label_cancel", "de" to "Abbrechen"),
        ),
    )

    @Test
    fun acceptsAFileWhoseKeysAllStartWithThePrefix() {
        bitsStringFile(section("Login", singular("login_label_ok", "de" to "OK")))
            .requireKeyPrefix("login_", "strings.ini")
    }

    @Test
    fun namesEveryKeyWithoutThePrefixAcrossSections() {
        val error = assertFailsWith<GradleException> {
            file.requireKeyPrefix("login_", "feature/strings.ini")
        }

        val message = error.message.orEmpty()
        assertContains(message, "feature/strings.ini")
        assertContains(message, "'login_'")
        assertContains(message, "general_label_cancel")
        assertFalse(message.contains("login_label_ok"), message)
    }
}
