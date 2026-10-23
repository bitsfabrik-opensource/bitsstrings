package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.transformers.IosLegacyTransformer
import org.gradle.api.GradleException
import java.io.File


/**
 * Writes `<lang>.lproj/<tableName>.strings`; the `.lproj` directories are shared by all modules of
 * an app, so only this table's files are ever replaced.
 */
internal class IosLegacyResourceFilesGenerator(
    private val tableName: String,
) : PlatformStringResourcesGenerator {

    override fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>> {
        try {
            return IosLegacyTransformer().processBitString(bitsStringFile)
                .flatMap { (language, content) ->
                    listOfNotNull(
                        File("$language$LPROJ_SUFFIX/${stringsFileName()}") to content,

                        //TODO default language
                    )

                }
        } catch (err: ResourceFileGenerationError) {
            throw GradleException("ResourceFile Generation Failed! ${err.message}")
        }
    }

    override fun ownedFiles(outputDir: File): List<File> =
        outputDir.listFiles { file -> file.isDirectory && file.name.endsWith(LPROJ_SUFFIX) }
            .orEmpty()
            .map { File(it, stringsFileName()) }

    private fun stringsFileName() = "$tableName.strings"

    private companion object {
        const val LPROJ_SUFFIX = ".lproj"
    }
}
