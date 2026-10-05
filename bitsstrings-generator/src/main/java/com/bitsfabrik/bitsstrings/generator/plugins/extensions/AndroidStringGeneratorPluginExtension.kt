package com.bitsfabrik.bitsstrings.generator.plugins.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property

@Suppress("UnnecessaryAbstractClass")
abstract class AndroidStringGeneratorPluginExtension {
    abstract val resourcesPackage: Property<String>
    abstract val resourcesClassName: Property<String>
    abstract val stringsIniPath: Property<String>
    abstract val defaultLang: Property<String>
}

internal fun AndroidStringGeneratorPluginExtension.setupConvention(project: Project) {
    resourcesPackage.convention(project.provider { "${project.group}.${project.name}" })
    resourcesClassName.convention(GeneratorDefaults.RESOURCES_CLASS_NAME)
    stringsIniPath.convention(GeneratorDefaults.STRINGS_INI_PATH)
    defaultLang.convention(GeneratorDefaults.DEFAULT_LANG)
}
