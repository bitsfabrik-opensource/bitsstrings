package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.transformers.AndroidXmlTransformer
import org.gradle.api.GradleException
import java.io.File


internal class AndroidResourceFilesGenerator : PlatformStringResourcesGenerator {

    override fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>> {
        try {
            return AndroidXmlTransformer().processBitString(bitsStringFile)
                .flatMap { (language, content) ->
                    listOfNotNull(
                        File("values-$language/strings.xml") to content,

                        if (language == defaultLanguage) {
                            File("values/strings.xml") to content
                        } else {
                            null
                        }
                    )

                }
        } catch (err: ResourceFileGenerationError) {
            throw GradleException("ResourceFile Generation Failed! ${err.message}")
        }
    }
}
