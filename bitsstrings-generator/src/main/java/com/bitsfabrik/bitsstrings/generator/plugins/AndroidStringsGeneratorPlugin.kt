package com.bitsfabrik.bitsstrings.generator.plugins

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.Sources
import com.android.build.api.variant.Variant
import com.bitsfabrik.bitsstrings.generator.plugins.extensions.AndroidStringGeneratorPluginExtension
import com.bitsfabrik.bitsstrings.generator.plugins.extensions.setupConvention
import com.bitsfabrik.bitsstrings.generator.tasks.GenerateAndroidStringsTask
import com.bitsfabrik.bitsstrings.generator.tasks.registration.registerAndroidGenerateTask
import com.bitsfabrik.bitsstrings.generator.domain.AndroidPluginIds
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

open class AndroidStringsGeneratorPlugin : Plugin<Project> {

    companion object {
        private const val EXTENSION_NAME = "bitsfabrikAndroidStrings"
        private const val TASK_GROUP = "com.bitsfabrik.strings"
        private const val AGGREGATE_TASK_NAME = "generateAndroidStrings"
        private const val IDEA_IMPORT_TASK_NAME = "prepareKotlinIdeaImport"
        private const val ANDROID_PRE_BUILD_TASK_NAME = "preBuild"
    }

    override fun apply(project: Project) {
        val extension: AndroidStringGeneratorPluginExtension = project.extensions.create(
            name = EXTENSION_NAME,
            type = AndroidStringGeneratorPluginExtension::class
        ).apply { setupConvention(project) }

        listOf(AndroidPluginIds.APPLICATION, AndroidPluginIds.LIBRARY).forEach { id ->
            project.plugins.withId(id) {
                registerForAndroidProject(project, extension)
            }
        }
    }

    private fun registerForAndroidProject(
        project: Project,
        extension: AndroidStringGeneratorPluginExtension
    ) {
        val genTaskProvider: TaskProvider<GenerateAndroidStringsTask> =
            registerAndroidGenerateTask(project, extension)

        registerAggregateTask(project, genTaskProvider)
        setupAndroidVariantsSync(project, genTaskProvider)
        runGenerationOnGradleSync(project, genTaskProvider)
    }

    /** Umbrella task so that `./gradlew generateAndroidStrings` regenerates everything. */
    private fun registerAggregateTask(
        project: Project,
        genTaskProvider: TaskProvider<GenerateAndroidStringsTask>
    ) {
        project.tasks.register(AGGREGATE_TASK_NAME) {
            it.group = TASK_GROUP
            it.dependsOn(genTaskProvider)
        }
    }

    private fun setupAndroidVariantsSync(
        project: Project,
        genTaskProvider: TaskProvider<GenerateAndroidStringsTask>
    ) {
        val componentsExtension: AndroidComponentsExtension<*, *, *> = project.extensions
            .findByType<LibraryAndroidComponentsExtension>()
            ?: project.extensions.findByType<ApplicationAndroidComponentsExtension>()
            ?: project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: error("AndroidComponentsExtension not found")

        componentsExtension.onVariants { variant: Variant ->
            variant.sources.addGeneratedSources(genTaskProvider)
        }

        project.tasks
            .matching { it.name == ANDROID_PRE_BUILD_TASK_NAME }
            .configureEach { it.dependsOn(genTaskProvider) }
    }

    /** Makes a gradle sync regenerate the mappings, so the IDE never sees stale code. */
    private fun runGenerationOnGradleSync(
        project: Project,
        genTaskProvider: TaskProvider<GenerateAndroidStringsTask>
    ) {
        project.tasks.matching { it.name == IDEA_IMPORT_TASK_NAME }.configureEach {
            it.dependsOn(genTaskProvider)
        }
    }
}

internal fun Sources.addGeneratedSources(
    provider: TaskProvider<GenerateAndroidStringsTask>
) {
    @Suppress("UnstableApiUsage")
    kotlin?.addGeneratedSourceDirectory(
        taskProvider = provider,
        wiredWith = GenerateAndroidStringsTask::outputSourcesDir
    )

    java?.addGeneratedSourceDirectory(
        taskProvider = provider,
        wiredWith = GenerateAndroidStringsTask::outputSourcesDir
    )

    res?.addGeneratedSourceDirectory(
        taskProvider = provider,
        wiredWith = GenerateAndroidStringsTask::androidResourcesDir
    )
}
