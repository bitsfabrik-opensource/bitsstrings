package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import java.io.File
import java.io.IOException


internal class ResourceFilesGenerator(
    private val outputSourcesDir: File,
    private val generator: PlatformStringResourcesGenerator,
) {

    /**
     * Writes the generated files and removes the owned files that are no longer generated. A file
     * whose content did not change is left untouched, so its timestamp stays the same and nothing
     * that depends on it (an Xcode build phase, a file watcher) sees a change.
     */
    fun generateResources(bitsStringFile: BitsStringFile, defaultLanguage: String) {
        val files = generator.generateResources(bitsStringFile, defaultLanguage)
            .associate { (file, content) -> File(outputSourcesDir, file.path).normalized() to content }

        removeStaleFiles(keep = files.keys)

        files.forEach { (file, content) -> writeIfChanged(content, file) }
    }

    private fun removeStaleFiles(keep: Set<File>) {
        val root = outputSourcesDir.normalized()

        generator.ownedFiles(outputSourcesDir).forEach { owned ->
            owned.normalized().walkBottomUp().forEach { file ->
                val isStaleFile = file.isFile && file !in keep
                val isEmptyDirectory = file.isDirectory && file != root && file.list().isNullOrEmpty()

                if (isStaleFile || isEmptyDirectory) file.delete()
            }
        }
    }

    private fun writeIfChanged(output: String, file: File) {
        try {
            if (file.isFile && file.readText(Charsets.UTF_8) == output) return

            file.parentFile?.mkdirs()
            file.writeText(output, Charsets.UTF_8)
        } catch (e: IOException) {
            throw ResourceFileGenerationError("Creation of file ${file.absolutePath} failed.")
        }
    }

    private fun File.normalized(): File = absoluteFile.normalize()
}
