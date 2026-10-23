package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier

/**
 * `iosMain`: resolves through the resource key of the generated `.strings` / `.xcstrings` file,
 * looked up in this module's own [tableName].
 */
internal class AppleStringMappingsGenerator(
    private val tableName: String,
) : AbstractPlatformStringMappingsGenerator(
    typeModifiers = listOf(KModifier.ACTUAL),
    propertyModifiers = listOf(KModifier.ACTUAL),
) {

    override fun generateInitializer(metadata: StringMetaData): CodeBlock {
        return if (metadata.isPlural) {
            CodeBlock.of("PluralsResource(resourceId = %S, tableName = %S)", metadata.apple, tableName)
        } else {
            CodeBlock.of("StringResource(resourceId = %S, tableName = %S)", metadata.apple, tableName)
        }
    }
}
