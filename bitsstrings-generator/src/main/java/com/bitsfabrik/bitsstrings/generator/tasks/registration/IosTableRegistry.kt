package com.bitsfabrik.bitsstrings.generator.tasks.registration

import com.bitsfabrik.bitsstrings.generator.domain.IosTableClaims
import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.File

/** Build wide [IosTableClaims], shared by every project that applies the plugin. */
internal abstract class IosTableRegistry : BuildService<BuildServiceParameters.None> {
    private val claims = IosTableClaims()

    fun claim(iosResourcesDir: File, tableName: String, projectPath: String) =
        claims.claim(iosResourcesDir, tableName, projectPath)
}

private const val SERVICE_NAME = "bitsstringsIosTables"

internal fun Project.claimIosTable(iosResourcesDir: File, tableName: String) {
    gradle.sharedServices
        .registerIfAbsent(SERVICE_NAME, IosTableRegistry::class.java) {}
        .get()
        .claim(iosResourcesDir, tableName, path)
}
