package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier

/** `commonMain`: the `expect object` — declarations only, members inherit `expect` from the object. */
internal class CommonStringMappingsGenerator : AbstractPlatformStringMappingsGenerator(
    typeModifiers = listOf(KModifier.EXPECT),
    propertyModifiers = emptyList(),
) {
    override fun generateInitializer(metadata: StringMetaData): CodeBlock? = null
}
