package com.bitsfabrik.bitsstrings.generator.tasks

import com.bitsfabrik.bitsstrings.generator.domain.GeneratedSourceSet
import com.bitsfabrik.bitsstrings.generator.generators.mappings.AndroidStringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.mappings.AppleStringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.mappings.CommonStringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.mappings.PlatformStringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.mappings.StringMappingsGenerator
import com.bitsfabrik.bitsstrings.generator.generators.resources.ResourceFilesGenerator
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.generators.resources.AndroidResourceFilesGenerator
import com.bitsfabrik.bitsstrings.generator.generators.resources.IosLegacyResourceFilesGenerator
import com.bitsfabrik.bitsstrings.generator.generators.resources.IosStringCatalogResourceFileGenerator
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Generates the mappings and platform resource files for one KMP source set. */
abstract class GenerateBitsStringsTask : AbstractGenerateStringsTask() {

    /** Decides which platform flavour of the mappings is generated, see [GeneratedSourceSet]. */
    @get:Input
    abstract val sourceSetName: Property<String>

    @get:Optional
    @get:Input
    abstract val androidRClassPackage: Property<String>

    @get:Input
    abstract val legacyGeneration: Property<Boolean>

    /** Only set for the ios source set, see [androidResourcesDir]. */
    @get:Optional
    @get:OutputDirectory
    abstract val iosResourcesDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val bitsStringFile = parseStringsIniOrNull() ?: return

        val sourceSet = GeneratedSourceSet.ofSourceSet(sourceSetName.get()) ?:  throw GradleException("Unexpected Sourceset Name: ${sourceSetName.get()}")

        generateMappings(bitsStringFile, sourceSet)
        generateResourceFiles(bitsStringFile, sourceSet)
    }

    private fun generateMappings(bitsStringFile: BitsStringFile, sourceSet: GeneratedSourceSet) {
        StringMappingsGenerator(
            outputSourcesDir.get().asFile,
            platformStringGenerator(sourceSet),
            legacy = legacyGeneration.get(),
            resourcesPackageName.get(),
            resourcesClassName.get()
        ).generateMappings(bitsStringFile)
    }

    private fun generateResourceFiles(bitsStringFile: BitsStringFile, sourceSet: GeneratedSourceSet) {
        val generator = when(sourceSet){
            GeneratedSourceSet.ANDROID -> ResourceFilesGenerator(
                androidResourcesDir.get().asFile,
                AndroidResourceFilesGenerator(),
            )

            GeneratedSourceSet.IOS -> {
                val gen = if(legacyGeneration.get()) {
                    IosLegacyResourceFilesGenerator()
                } else{
                    IosStringCatalogResourceFileGenerator()
                }

                ResourceFilesGenerator(
                    iosResourcesDir.get().asFile,
                    gen
                )
            }
            else->{
                // no resources for common sourceSet
                null
            }
        }

        generator?.generateResources(bitsStringFile, defaultLanguage.get())
    }

    private fun platformStringGenerator(sourceSet: GeneratedSourceSet): PlatformStringMappingsGenerator {
        return when (sourceSet) {
            GeneratedSourceSet.COMMON -> CommonStringMappingsGenerator()
            GeneratedSourceSet.ANDROID -> AndroidStringMappingsGenerator(
                androidRClassPackage.get(),
                isActual = true
            )

            GeneratedSourceSet.IOS -> AppleStringMappingsGenerator()
        }
    }

}
