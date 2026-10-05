package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key

import kotlin.test.Test
import kotlin.test.assertEquals

class KeyFormatterTest {

    @Test
    fun androidKeepsAnAlreadySnakeCasedKey() {
        assertEquals("general_label_ok", AndroidKeyFormatter.formatKey("general_label_ok"))
    }

    @Test
    fun androidSplitsCamelCaseIntoSnakeCase() {
        assertEquals(
            "login_label_password_reset_successful",
            AndroidKeyFormatter.formatKey("login_label_passwordResetSuccessful")
        )
    }

    @Test
    fun androidLowercasesEveryUpperCaseCharacterIndividually() {
        // consecutive capitals each get their own separator — the documented behaviour of the regex
        assertEquals("_u_r_l", AndroidKeyFormatter.formatKey("URL"))
    }

    @Test
    fun androidLeavesDigitsAndUnderscoresAlone() {
        assertEquals("error_404_not_found", AndroidKeyFormatter.formatKey("error_404_not_found"))
    }

    @Test
    fun iosReplacesUnderscoresWithDots() {
        assertEquals("general.label.ok", IosKeyFormatter.formatKey("general_label_ok"))
    }

    @Test
    fun iosPreservesCasing() {
        assertEquals(
            "login.label.PasswordReset",
            IosKeyFormatter.formatKey("login_label_PasswordReset")
        )
    }

    @Test
    fun iosLeavesAKeyWithoutUnderscoresUnchanged() {
        assertEquals("ok", IosKeyFormatter.formatKey("ok"))
    }
}
