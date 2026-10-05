package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key

/** `login_label_passwordReset` → `login_label_password_reset` */
internal object AndroidKeyFormatter : KeyFormatter {

    private val upperCaseRegex = "([A-Z])".toRegex()

    override fun formatKey(key: String): String =
        upperCaseRegex.replace(key) { match -> "_${match.value.lowercase()}" }
}