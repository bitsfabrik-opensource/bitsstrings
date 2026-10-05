package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.transformers.IosLegacyTransformer
import org.gradle.api.GradleException
import java.io.File


internal class IosLegacyResourceFilesGenerator : PlatformStringResourcesGenerator {

    override fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>> {
        try {
            return IosLegacyTransformer().processBitString(bitsStringFile)
                .flatMap { (language, content) ->
                    listOfNotNull(
                        File("$language.lproj/Localizable.strings") to content,

                        //TODO default language
                    )

                }
        } catch (err: ResourceFileGenerationError) {
            throw GradleException("ResourceFile Generation Failed! ${err.message}")
        }
    }
}
