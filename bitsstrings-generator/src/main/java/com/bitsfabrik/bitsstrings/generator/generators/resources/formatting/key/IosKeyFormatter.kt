package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key

/** `login_label_passwordReset` → `login.label.passwordReset` */
internal object IosKeyFormatter : KeyFormatter {
    override fun formatKey(key: String): String = key.replace("_", ".")
}