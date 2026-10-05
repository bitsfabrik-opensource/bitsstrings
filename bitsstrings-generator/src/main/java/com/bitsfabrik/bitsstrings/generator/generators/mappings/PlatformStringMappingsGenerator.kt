package com.bitsfabrik.bitsstrings.generator.generators.mappings

import com.bitsfabrik.bitsstrings.generator.domain.StringMetaData
import com.squareup.kotlinpoet.FileSpec

internal interface PlatformStringMappingsGenerator {
    fun generateClass(
        builder: FileSpec.Builder,
        resourceClassName: String,
        metadata: List<StringMetaData>
    )
}

