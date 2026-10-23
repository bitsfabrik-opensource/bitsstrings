package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import java.io.File

internal interface PlatformStringResourcesGenerator {
    fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>>

    /**
     * The files in [outputDir] that belong to this generator. They are deleted before writing, so
     * the output of a language that no longer exists does not linger. Defaults to the whole
     * [outputDir], which only fits a directory the generator owns exclusively.
     */
    fun ownedFiles(outputDir: File): List<File> = listOf(outputDir)
}
