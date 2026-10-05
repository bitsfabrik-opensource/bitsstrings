package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key.AndroidKeyFormatter
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key.IosKeyFormatter
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.squareup.kotlinpoet.FileSpec
import java.io.File

/** Writes the typed kotlin accessors for one source set into [outputSourcesDir]. */
internal class StringMappingsGenerator(
    private val outputSourcesDir: File,
    private val generator: PlatformStringMappingsGenerator,
    private val legacy: Boolean,
    private val resourcesPackageName: String,
    private val resourcesClassName: String
) {

    fun generateMappings(bitsStringFile: BitsStringFile) {
        outputSourcesDir.deleteRecursively()

        val fileSpec: FileSpec.Builder = FileSpec.builder(
            packageName = resourcesPackageName,
            fileName = resourcesClassName
        )

        generator.generateClass(fileSpec, resourcesClassName, bitsStringFile.stringMetaData())

        fileSpec.build().writeTo(outputSourcesDir)
    }

    private fun BitsStringFile.stringMetaData(): List<StringMetaData> =
        sections.flatMap { it.stringDefinitions }.map { createStringMetaDataFor(it) }

    private fun createStringMetaDataFor(definition: BitsStringDefinition): StringMetaData {
        return StringMetaData(
            key = definition.key,
            android = AndroidKeyFormatter.formatKey(definition.key),
            // the string catalog keys off the raw ini key, only the legacy .strings format maps it
            apple = if (legacy) IosKeyFormatter.formatKey(definition.key) else definition.key,
            isPlural = definition is BitsStringDefinition.Plural
        )
    }
}
