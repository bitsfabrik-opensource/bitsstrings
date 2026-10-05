package com.bitsfabrik.bitsstrings.generator.domain

internal sealed class BitsStringDefinition {
    abstract val key: String

    internal data class Singular(
        override val key: String,
        val translations: Map<String, String>
    ) : BitsStringDefinition()

    internal data class Plural(
        override val key: String,
        val translations: Map<String, Map<String, String>>
    ) : BitsStringDefinition()

    class Builder(private val key: String) {
        private val singularTranslations = mutableMapOf<String, String>()
        private val pluralMap = mutableMapOf<String, MutableMap<String, String>>()
        private var activeQuantity: String? = null

        fun addTranslation(lang: String, value: String) {
            val qty = activeQuantity
            if (qty == null) {
                singularTranslations[lang] = value
            } else {
                pluralMap.getOrPut(qty) { mutableMapOf() }[lang] = value
            }
        }

        fun startQuantity(quantity: String) {
            activeQuantity = quantity
        }

        fun build(): BitsStringDefinition {
            return when {
                pluralMap.isNotEmpty() -> Plural(key, pluralMap.toMap())
                else -> Singular(key, singularTranslations.toMap())
            }
        }
    }
}

internal data class BitsStringSection(
    val title: String,
    val stringDefinitions: List<BitsStringDefinition>
) {
    class Builder(private val title: String) {
        private val definitions = mutableListOf<BitsStringDefinition.Builder>()

        fun addDefinition(def: BitsStringDefinition.Builder) {
            definitions.add(def)
        }

        fun build() = BitsStringSection(title, definitions.map { it.build() })
    }
}

internal data class BitsStringFile(
    val sections: List<BitsStringSection>
) {
    class Builder {
        private val sections = mutableListOf<BitsStringSection.Builder>()

        fun addSection(def: BitsStringSection.Builder) {
            sections.add(def)
        }

        fun build() = BitsStringFile(sections.map { it.build() })
    }

    fun getLanguages(): Set<String> {
        return sections.flatMap { s ->
            s.stringDefinitions.flatMap { d ->
                when(d) {
                    is BitsStringDefinition.Plural -> d.translations.values.flatMap { it.keys }
                    is BitsStringDefinition.Singular -> d.translations.keys
                }
            }
        }.toSet()
    }
}