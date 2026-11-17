package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.transformers.IosStringDictTransformer
import org.gradle.api.GradleException
import java.io.File


/**
 * Writes `<tableName>.xcstrings` in the exact format Xcode saves it in, see [XcodeJsonWriter]; the
 * ios resources dir is shared by all modules of an app.
 */
internal class IosStringCatalogResourceFileGenerator(
    private val tableName: String,
) : PlatformStringResourcesGenerator {

    override fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>> {
        try {
            val catalog = IosStringDictTransformer().processBitString(bitsStringFile, defaultLanguage)

            return listOf(catalogFile() to XcodeJsonWriter.write(catalog))

        } catch (err: ResourceFileGenerationError) {
            throw GradleException("ResourceFile Generation Failed! ${err.message}")
        }
    }

    override fun ownedFiles(outputDir: File): List<File> =
        listOf(File(outputDir, catalogFile().path))

    private fun catalogFile() = File("$tableName.xcstrings")
}
