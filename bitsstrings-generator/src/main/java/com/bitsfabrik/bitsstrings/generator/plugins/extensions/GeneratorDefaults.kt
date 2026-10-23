package com.bitsfabrik.bitsstrings.generator.plugins.extensions

/** Conventions shared by the KMP and the android-only plugin extension. */
internal object GeneratorDefaults {
    const val RESOURCES_CLASS_NAME = "BitsStrings"
    const val STRINGS_INI_PATH = "../strings.ini"
    const val IOS_RESOURCES_DIR = "../iosApp"

    /** The table ios resolves from without a table name — keeps single-module setups unchanged. */
    const val IOS_TABLE_NAME = "Localizable"
    const val DEFAULT_LANG = "de"
    const val LEGACY_GENERATION = false
}
