package com.bitsfabrik.bitsstrings.generator.generators.resources.transformers

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.ResourceFileGenerationError
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value.IosValueFormatter
import com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value.ValueFormatter
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * Builds a single `Localizable.xcstrings` catalog holding every language and plural variation.
 * [com.bitsfabrik.bitsstrings.generator.generators.resources.XcodeJsonWriter] turns it into text.
 */
internal class IosStringDictTransformer(
    private val valueFormatter: ValueFormatter = IosValueFormatter,
) {

    fun processBitString(bitsStringFile: BitsStringFile, defaultLanguage: String): JsonObject {
        val languages = bitsStringFile.getLanguages()

        return buildJsonObject {
            put("sourceLanguage", defaultLanguage)
            putJsonObject("strings") {
                bitsStringFile.sections
                    .flatMap { it.stringDefinitions }
                    .forEach { stringDef ->
                        putJsonObject(stringDef.key) {
                            put("extractionState", "manual")
                            put(
                                "localizations",
                                when (stringDef) {
                                    is BitsStringDefinition.Plural -> mapPlural(stringDef, languages)
                                    is BitsStringDefinition.Singular -> mapSingular(stringDef)
                                }
                            )
                        }
                    }
            }
            put("version", "1.0")
        }
    }

    private fun mapSingular(stringDef: BitsStringDefinition.Singular): JsonObject =
        buildJsonObject {
            stringDef.translations.forEach { (language, value) ->
                putJsonObject(language) { put("stringUnit", stringUnit(stringDef.key, value)) }
            }
        }

    private fun mapPlural(stringDef: BitsStringDefinition.Plural, languages: Set<String>): JsonObject =
        buildJsonObject {
            languages.forEach { language ->
                val variations = stringDef.translations.mapNotNull { (quantity, translation) ->
                    translation[language]?.let { value -> quantity to value }
                }

                // a plural does not have to be translated into every language of the file
                if (variations.isEmpty()) return@forEach

                putJsonObject(language) {
                    putJsonObject("variations") {
                        putJsonObject("plural") {
                            variations.forEach { (quantity, value) ->
                                putJsonObject(quantity) {
                                    put("stringUnit", stringUnit(stringDef.key, value))
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun stringUnit(key: String, value: String): JsonObject = buildJsonObject {
        put("state", "translated")
        put("value", formatValue(key, value))
    }

    /**
     * The formatted value is read as the content of a json string literal, exactly like the catalog
     * used to be written as raw json text: escapes such as `\n` in the strings.ini keep working.
     */
    private fun formatValue(key: String, value: String): JsonPrimitive {
        val literal = "\"" + valueFormatter.formatValue(value) + "\""
        return try {
            JsonPrimitive(Json.parseToJsonElement(literal).jsonPrimitive.content)
        } catch (e: SerializationException) {
            throw ResourceFileGenerationError("Invalid value of '$key': $value (${e.message})")
        }
    }
}
