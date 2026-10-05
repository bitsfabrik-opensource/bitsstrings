package com.bitsfabrik.bitsstrings.generator.domain

/**
 * The KMP source sets the plugin generates into — one `expect` set plus one `actual` set per
 * platform. Every other source set of a consumer project is left alone.
 */
internal enum class GeneratedSourceSet(val sourceSetName: String) {
    COMMON("commonMain"),
    ANDROID("androidMain"),
    IOS("iosMain");

    companion object {
        val names: Set<String> = entries.mapTo(linkedSetOf()) { it.sourceSetName }

        fun ofSourceSet(sourceSetName: String): GeneratedSourceSet? =
            entries.firstOrNull { it.sourceSetName == sourceSetName }
    }
}
