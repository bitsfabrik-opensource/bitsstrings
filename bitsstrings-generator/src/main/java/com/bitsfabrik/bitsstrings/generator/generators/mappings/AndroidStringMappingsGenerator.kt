package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier

/**
 * `androidMain`: resolves through the generated `R` class. [isActual] is false for the android-only
 * plugin, where the object is a plain object instead of the `actual` of a KMP `expect`.
 */
internal class AndroidStringMappingsGenerator(
    private val androidRClassPackage: String,
    isActual: Boolean,
) : AbstractPlatformStringMappingsGenerator(
    typeModifiers = actualModifierIf(isActual),
    propertyModifiers = actualModifierIf(isActual),
) {

    override fun addImports(builder: FileSpec.Builder) {
        builder.addImport(androidRClassPackage, "R")
    }

    override fun generateInitializer(metadata: StringMetaData): CodeBlock {
        return if (metadata.isPlural) {
            CodeBlock.of("PluralsResource(R.plurals.%L)", metadata.android)
        } else {
            CodeBlock.of("StringResource(R.string.%L)", metadata.android)
        }
    }
}

private fun actualModifierIf(isActual: Boolean): List<KModifier> =
    if (isActual) listOf(KModifier.ACTUAL) else emptyList()
