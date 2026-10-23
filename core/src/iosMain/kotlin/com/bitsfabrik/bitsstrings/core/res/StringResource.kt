package com.bitsfabrik.bitsstrings.core.res

/**
 * [tableName] is the strings table (`<tableName>.xcstrings` / `<tableName>.strings`) the key lives
 * in; `null` resolves from `Localizable`. Every module generates its own table, so the keys of
 * different modules never collide in the app bundle.
 */
actual data class StringResource(
    val resourceId: String,
    val tableName: String? = null
)
