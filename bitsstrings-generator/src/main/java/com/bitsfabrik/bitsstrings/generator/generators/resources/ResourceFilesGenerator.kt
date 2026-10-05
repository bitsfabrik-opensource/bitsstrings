package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import java.io.File
import java.io.IOException


internal class ResourceFilesGenerator(
    private val outputSourcesDir: File,
    private val generator: PlatformStringResourcesGenerator,
) {

    fun generateResources(bitsStringFile: BitsStringFile, defaultLanguage: String) {
        outputSourcesDir.deleteRecursively()

        val files = generator.generateResources(bitsStringFile, defaultLanguage)

        files.forEach { (file, content)->
            writeGeneratedResourceFile(content, File(outputSourcesDir, file.path))
        }

    }

    private fun writeGeneratedResourceFile(output: String, file: File) {
        file.parentFile?.mkdirs()
        try {
            file.writeText(output, Charsets.UTF_8)
        } catch (e: IOException) {
            throw ResourceFileGenerationError("Creation of file ${file.absolutePath} failed.")
        }
    }
}
