package com.bitsfabrik.bitsstrings.generator.tasks

import com.bitsfabrik.bitsstrings.generator.generators.mappings.AndroidStringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.mappings.StringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.AndroidResourceFilesGenerator
import com.bitsfabrik.bitsstrings.generator.generators.resources.ResourceFilesGenerator
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/** Generates the mappings and `res` files of a plain android (non-KMP) module. */
abstract class GenerateAndroidStringsTask : AbstractGenerateStringsTask() {

    @get:Input
    abstract val androidRClassPackage: Property<String>

    @TaskAction
    fun generate() {
        val bitsStringFile = parseStringsIniOrNull() ?: return

        generateMappings(bitsStringFile)
        generateResourceFiles(bitsStringFile)
    }

    private fun generateMappings(bitsStringFile: BitsStringFile) {
        StringMappingsGenerator(
            outputSourcesDir.get().asFile,
            AndroidStringMappingsGenerator(androidRClassPackage.get(), isActual = false),
            legacy = false,
            resourcesPackageName.get(),
            resourcesClassName.get()
        ).generateMappings(bitsStringFile)
    }

    private fun generateResourceFiles(bitsStringFile: BitsStringFile) {
        ResourceFilesGenerator(
            androidResourcesDir.get().asFile,
            AndroidResourceFilesGenerator()
        ).generateResources(bitsStringFile, defaultLanguage.get())
    }
}
