package com.bitsfabrik.bitsstrings.generator.generators.resources

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.transformers.IosStringDictTransformer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.gradle.api.GradleException
import java.io.File


internal class IosStringCatalogResourceFileGenerator : PlatformStringResourcesGenerator {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    override fun generateResources(
        bitsStringFile: BitsStringFile,
        defaultLanguage: String
    ): List<Pair<File, String>> {
        try {
            val transformer = IosStringDictTransformer()
            val catalog = transformer.processBitString(bitsStringFile, defaultLanguage)
            val prettyPrinted = json.encodeToString(
                JsonElement.serializer(),
                Json.parseToJsonElement(catalog)
            )

            return listOf(File("Localizable.xcstrings") to prettyPrinted)

        } catch (err: ResourceFileGenerationError) {
            throw GradleException("ResourceFile Generation Failed! ${err.message}")
        }
    }
}
