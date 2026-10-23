package com.bitsfabrik.bitsstrings.generator.domain

import org.gradle.api.GradleException
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Every ios strings table belongs to exactly one module. Two modules writing the same table into
 * the same directory would silently overwrite each other's keys, so the second claim fails.
 */
internal class IosTableClaims {
    private val owners = ConcurrentHashMap<String, String>()

    fun claim(iosResourcesDir: File, tableName: String, projectPath: String) {
        val table = File(iosResourcesDir, tableName).canonicalPath
        val owner = owners.putIfAbsent(table, projectPath) ?: return
        if (owner == projectPath) return

        throw GradleException(
            "bitsstrings: '$projectPath' and '$owner' both generate the ios table '$tableName' " +
                    "into ${iosResourcesDir.path}. Set a unique iosTableName in one of them."
        )
    }
}
