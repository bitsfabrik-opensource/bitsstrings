package com.bitsfabrik.bitsstrings.generator.generators.resources.transformers

import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value.IosValueFormatter
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value.ValueFormatter
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile

/**
 * Renders a single `Localizable.xcstrings` catalog holding every language and plural variation.
 * Emits raw json text; [com.bitsfabrik.bitsstrings.generator.generators.resources.ResourceFilesGenerator] pretty-prints it before writing.
 */
internal class IosStringDictTransformer(
    private val valueFormatter: ValueFormatter = IosValueFormatter,
) {

    fun formatValue(value: String): String = valueFormatter.formatValue(value)

    fun processBitString(bitsStringFile: BitsStringFile, defaultLanguage: String): String {
        val languages = bitsStringFile.getLanguages()

        val definitionsContent = bitsStringFile.sections
            .flatMap { it.stringDefinitions }
            .joinToString(",\n") { stringDef ->
                val content = when (stringDef) {
                    is BitsStringDefinition.Plural -> mapPlural(stringDef, languages)
                    is BitsStringDefinition.Singular -> mapSingular(stringDef)
                }

                stringHeader(stringDef.key) + content + stringFooter()
            }

        return fileHeader(defaultLanguage) + definitionsContent + fileFooter()
    }

    private fun mapSingular(stringDef: BitsStringDefinition.Singular): String {
        val languageEntries = stringDef.translations.map { (language, value) ->
            "\"$language\": { \n" + stringUnit(value) + "}"
        }

        return languageEntries.joinToString(",\n") + "\n"
    }

    private fun mapPlural(stringDef: BitsStringDefinition.Plural, languages: Set<String>): String {
        val languageEntries = languages.mapNotNull { language ->
            val variations = stringDef.translations.mapNotNull { (quantity, translation) ->
                translation[language]?.let { value ->
                    "\"$quantity\": {\n" + stringUnit(value) + "}"
                }
            }

            // a plural does not have to be translated into every language of the file
            if (variations.isEmpty()) return@mapNotNull null

            "\"$language\": {\n" +
                    "\"variations\": {\n" +
                    "\"plural\": {\n" +
                    variations.joinToString(",\n") + "\n" +
                    "}\n" +
                    "}\n" +
                    "}"
        }

        return languageEntries.joinToString(",\n") + "\n"
    }

    private fun stringUnit(value: String): String {
        return "\"stringUnit\": {\n" +
                "\"state\": \"translated\",\n" +
                "\"value\": \"" + formatValue(value) + "\"\n" +
                "}\n"
    }

    private fun fileHeader(language: String): String {
        return "{" + "\n" +
                "\"sourceLanguage\": \"${language}\",\n" +
                "\"strings\": {\n"
    }

    private fun fileFooter(): String {
        return "},\n" +
                "\"version\": \"1.0\"\n" +
                "}"
    }

    private fun stringHeader(key: String): String {
        return "\"${key}\": {\n" +
                "\"extractionState\": \"manual\",\n" +
                "\"localizations\": {\n"
    }

    private fun stringFooter(): String {
        return "}\n}"
    }
}