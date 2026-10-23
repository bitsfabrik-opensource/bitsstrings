package com.bitsfabrik.bitsstrings.generator.domain

import org.gradle.api.GradleException

/**
 * Android merges the string resources of all modules by name, so a key used by two modules silently
 * resolves to only one of them. A per-module prefix makes such collisions impossible.
 */
internal fun BitsStringFile.requireKeyPrefix(prefix: String, iniPath: String) {
    val invalidKeys = getKeys().filterNot { it.startsWith(prefix) }
    if (invalidKeys.isEmpty()) return

    throw GradleException(
        "${invalidKeys.size} key(s) in $iniPath do not start with the configured keyPrefix " +
                "'$prefix': ${invalidKeys.joinToString(", ")}"
    )
}
