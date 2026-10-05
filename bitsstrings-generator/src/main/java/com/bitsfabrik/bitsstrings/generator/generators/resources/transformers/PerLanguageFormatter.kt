package com.bitsfabrik.bitsstrings.generator.generators.resources.transformers

import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.key.KeyFormatter
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value.ValueFormatter
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile

/**
 * Base for the resource formats that write one file per language (android `strings.xml`, legacy
 * apple `Localizable.strings`). Owns the traversal of a [BitsStringFile]; subclasses only supply
 * the syntax of their format.
 */
internal abstract class PerLanguageTransformer(
    private val valueFormatter: ValueFormatter,
    private val keyFormatter: KeyFormatter,
) {

    protected fun formatKey(key: String): String = keyFormatter.formatKey(key)

    protected fun formatValue(value: String): String = valueFormatter.formatValue(value)

    /** The rendered file content per language contained in [bitsStringFile]. */
    fun processBitString(bitsStringFile: BitsStringFile): Map<String, String> =
        bitsStringFile.getLanguages().associateWith { language ->
            buildString {
                append(fileHeader(language))

                bitsStringFile.sections.forEach { section ->
                    append(sectionHeader(section.title))

                    section.stringDefinitions.forEach { definition ->
                        append(
                            when (definition) {
                                is BitsStringDefinition.Singular -> transformTranslation(
                                    definition.key,
                                    definition.translations[language].orEmpty()
                                )

                                is BitsStringDefinition.Plural -> transformPlural(definition, language)
                            }
                        )
                    }
                }

                append(fileFooter())
            }
        }

    protected abstract fun fileHeader(language: String): String
    protected abstract fun fileFooter(): String
    protected abstract fun sectionHeader(section: String): String
    protected abstract fun transformTranslation(key: String, value: String): String

    /** Rendered plural block, or a thrown [ResourceFileGenerationError] if the format has none. */
    protected abstract fun transformPlural(
        definition: BitsStringDefinition.Plural,
        language: String,
    ): String
}
