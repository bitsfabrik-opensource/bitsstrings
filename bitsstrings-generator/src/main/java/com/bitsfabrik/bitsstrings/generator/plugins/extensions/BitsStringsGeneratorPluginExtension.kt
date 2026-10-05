package com.bitsfabrik.bitsstrings.generator.plugins.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property

@Suppress("UnnecessaryAbstractClass")
abstract class BitsStringsGeneratorPluginExtension {
    abstract val resourcesPackage: Property<String>
    abstract val resourcesClassName: Property<String>
    abstract val stringsIniPath: Property<String>
    abstract val iosResourcesDir: Property<String>
    abstract val defaultLang: Property<String>
    abstract val legacyGeneration: Property<Boolean>
}

internal fun BitsStringsGeneratorPluginExtension.setupConvention(project: Project) {
    resourcesPackage.convention(project.provider { "${project.group}.${project.name}" })
    resourcesClassName.convention(GeneratorDefaults.RESOURCES_CLASS_NAME)
    stringsIniPath.convention(GeneratorDefaults.STRINGS_INI_PATH)
    iosResourcesDir.convention(GeneratorDefaults.IOS_RESOURCES_DIR)
    defaultLang.convention(GeneratorDefaults.DEFAULT_LANG)
    legacyGeneration.convention(GeneratorDefaults.LEGACY_GENERATION)
}