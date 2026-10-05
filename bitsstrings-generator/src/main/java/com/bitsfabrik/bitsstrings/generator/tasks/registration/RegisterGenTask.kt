package com.bitsfabrik.bitsstrings.generator.tasks.registration

import com.bitsfabrik.bitsstrings.generator.domain.GeneratedSourceSet
import com.bitsfabrik.bitsstrings.generator.plugins.extensions.BitsStringsGeneratorPluginExtension
import com.bitsfabrik.bitsstrings.generator.tasks.GenerateBitsStringsTask
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.tooling.core.extrasKeyOf
import java.io.File
import java.util.Locale

/**
 * A source set is visited once per compilation that uses it (`iosMain` is shared by every ios
 * target, for example). The registered task is cached on the source set so that every source set
 * ends up with exactly one generation task.
 */
private val GEN_TASK_KEY =
    extrasKeyOf<TaskProvider<GenerateBitsStringsTask>>("bitsstrings-generate-task")

internal fun KotlinSourceSet.getOrRegisterGenerateResourcesTask(
    extension: BitsStringsGeneratorPluginExtension,
): TaskProvider<GenerateBitsStringsTask> {
    return extras[GEN_TASK_KEY] ?: registerGenerateTask(extension).also {
        extras[GEN_TASK_KEY] = it
    }
}


/**
 * Registers `generateBitsStrings<SourceSetName>`, writing its mappings to
 * `build/generated/bitsstrings/<sourceSetName>/src` and — for the platform source sets — its resource
 * files to `build/generated/bitsstrings/androidMain/res` respectively the configured ios directory.
 */
private fun KotlinSourceSet.registerGenerateTask(
    extension: BitsStringsGeneratorPluginExtension,
): TaskProvider<GenerateBitsStringsTask> {
    val sourceSetName: String = name
    val generatedDir = "generated/bitsstrings/$sourceSetName"

    return project.tasks.register(
        getGenerationTaskName(),
        GenerateBitsStringsTask::class.java
    ) { task ->
        // the source set decides which platform flavour of the mappings is generated
        task.sourceSetName.set(sourceSetName)

        // input
        task.stringsIniFile.set(File(project.projectDir, extension.stringsIniPath.get()))
        task.defaultLanguage.set(extension.defaultLang)
        task.legacyGeneration.set(extension.legacyGeneration)

        // generated kotlin mappings
        task.resourcesPackageName.set(extension.resourcesPackage)
        task.resourcesClassName.set(extension.resourcesClassName)
        task.androidRClassPackage.set(project.getAndroidPackage())
        task.outputSourcesDir.set(project.layout.buildDirectory.dir("$generatedDir/src"))

        // generated platform resource files — only wired for the source set that owns them, so no
        // empty output directory is created for the source sets that generate mappings only
        when (GeneratedSourceSet.ofSourceSet(sourceSetName)) {
            GeneratedSourceSet.ANDROID ->
                task.androidResourcesDir.set(project.layout.buildDirectory.dir("$generatedDir/res"))

            GeneratedSourceSet.IOS ->
                task.iosResourcesDir.set(File(project.projectDir, extension.iosResourcesDir.get()))

            // commonMain holds the `expect` mappings and no resources
            GeneratedSourceSet.COMMON, null -> Unit
        }
    }
}


internal fun KotlinSourceSet.getGenerationTaskName(): String {
    val suffix = name.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else
            it.toString()
    }

    return "generateBitsStrings$suffix"
}
