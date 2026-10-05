package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * Emits the resources object of one platform: an object holding one `StringResource` /
 * `PluralsResource` property per ini key. The `expect` side only declares them, every `actual`
 * side initializes them from its own platform resource identifier.
 */
internal abstract class AbstractPlatformStringMappingsGenerator(
    private val typeModifiers: List<KModifier>,
    private val propertyModifiers: List<KModifier>,
) : PlatformStringMappingsGenerator {

    companion object {
        private const val CORE_RESOURCE_PACKAGE = "com.bitsfabrik.bitsstrings.core.res"

        private val stringResourceName = ClassName(CORE_RESOURCE_PACKAGE, "StringResource")
        private val pluralsResourceName = ClassName(CORE_RESOURCE_PACKAGE, "PluralsResource")
    }

    override fun generateClass(
        builder: FileSpec.Builder,
        resourceClassName: String,
        metadata: List<StringMetaData>
    ) {
        addImports(builder)

        val type = TypeSpec.Companion
            .objectBuilder(resourceClassName)
            .addModifiers(typeModifiers)
            .addProperties(metadata.map { createProperty(it) })

        builder.addType(type.build())
    }

    /** Imports the generated initializers need; nothing by default. */
    protected open fun addImports(builder: FileSpec.Builder) = Unit

    /** How this platform resolves [metadata], or `null` for the declaration-only `expect` side. */
    protected abstract fun generateInitializer(metadata: StringMetaData): CodeBlock?

    private fun createProperty(resource: StringMetaData): PropertySpec {
        val type = if (resource.isPlural) pluralsResourceName else stringResourceName

        return PropertySpec.Companion.builder(resource.key, type)
            .addModifiers(propertyModifiers)
            .apply { generateInitializer(resource)?.let { initializer(it) } }
            .build()
    }
}