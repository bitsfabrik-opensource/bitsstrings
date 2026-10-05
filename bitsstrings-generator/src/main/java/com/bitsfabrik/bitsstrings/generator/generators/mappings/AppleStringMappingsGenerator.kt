package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier

/** `iosMain`: resolves through the resource key of the generated `.strings` / `.xcstrings` file. */
internal class AppleStringMappingsGenerator : AbstractPlatformStringMappingsGenerator(
    typeModifiers = listOf(KModifier.ACTUAL),
    propertyModifiers = listOf(KModifier.ACTUAL),
) {

    override fun generateInitializer(metadata: StringMetaData): CodeBlock {
        return if (metadata.isPlural) {
            CodeBlock.of("PluralsResource(resourceId = %S)", metadata.apple)
        } else {
            CodeBlock.of("StringResource(resourceId = %S)", metadata.apple)
        }
    }
}
