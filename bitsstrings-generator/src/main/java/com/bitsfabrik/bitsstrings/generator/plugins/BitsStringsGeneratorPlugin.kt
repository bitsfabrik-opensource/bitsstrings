package com.bitsfabrik.bitsstrings.generator.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import com.bitsfabrik.bitsstrings.generator.domain.GeneratedSourceSet
import com.bitsfabrik.bitsstrings.generator.plugins.extensions.BitsStringsGeneratorPluginExtension
import com.bitsfabrik.bitsstrings.generator.plugins.extensions.setupConvention
import com.bitsfabrik.bitsstrings.generator.tasks.GenerateBitsStringsTask
import com.bitsfabrik.bitsstrings.generator.tasks.registration.getGenerationTaskName
import com.bitsfabrik.bitsstrings.generator.tasks.registration.getOrRegisterGenerateResourcesTask
import com.bitsfabrik.bitsstrings.generator.tasks.registration.getAndroidSourceSetOrNull
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget


class BitsStringsGeneratorPlugin : Plugin<Project> {

    companion object {
        private const val EXTENSION_NAME = "bitsstrings"
        private const val TASK_GROUP = "com.bitsfabrik.bitsstrings"
        private const val AGGREGATE_TASK_NAME = "generateBitsStrings"
        private const val IDEA_IMPORT_TASK_NAME = "prepareKotlinIdeaImport"
        private const val ANDROID_PRE_BUILD_TASK_NAME = "preBuild"
    }


    override fun apply(project: Project) {
        val extension: BitsStringsGeneratorPluginExtension = project.extensions.create(
            name = EXTENSION_NAME,
            type = BitsStringsGeneratorPluginExtension::class
        ).apply { setupConvention(project) }

        project.plugins.withType(KotlinMultiplatformPluginWrapper::class) {
            registerSources(project, extension)
            registerAndroidResources(project)
            registerAggregateTask(project)
            runGenerationOnGradleSync(project)
        }
    }

    /**
     * Registers one generation task per source set the plugin generates into (see
     * [GeneratedSourceSet]) and makes its kotlin mappings compile sources of that source set.
     */
    private fun registerSources(
        project: Project,
        extension: BitsStringsGeneratorPluginExtension
    ) {
        val kmpExtension: KotlinMultiplatformExtension = project.extensions.getByType()

        kmpExtension.sourceSets.configureEach { sourceSet: KotlinSourceSet ->
            if (sourceSet.name !in GeneratedSourceSet.names) return@configureEach
            val genTask = sourceSet.getOrRegisterGenerateResourcesTask(extension)

            // generated kotlin mappings become compile sources of the source set;
            // the provider carries the task dependency, the explicit dependsOn documents it
            sourceSet.kotlin.srcDir(genTask.flatMap { it.outputSourcesDir })
        }
    }

    /** Hands the generated android `res` tree to whichever AGP integration the consumer uses. */
    private fun registerAndroidResources(project: Project) {
        val kmpExtension: KotlinMultiplatformExtension = project.extensions.getByType()

        kmpExtension.targets.configureEach { target ->
            when (target) {
                is KotlinAndroidTarget -> target.compilations.configureEach { compilation ->
                    wireResourcesForCompilation(project, compilation, target, isKmpLibrary = false)
                }

                is KotlinMultiplatformAndroidLibraryTarget -> target.compilations.configureEach { compilation ->
                    wireResourcesForCompilation(project, compilation, target, isKmpLibrary = true)
                }
            }
        }
    }

    private fun wireResourcesForCompilation(
        project: Project,
        compilation: KotlinCompilation<*>,
        target: KotlinTarget,
        isKmpLibrary: Boolean
    ) {
        // Instead of asking the compilation for its source sets reactively (which we can't do),
        // we resolve what it actually compiles: its default source set plus the whole transitive
        // `dependsOn` closure. The hop count differs per AGP path — the KMP-android `main`
        // compilation defaults to `androidMain` and reaches `commonMain` in one hop, while the
        // legacy `debug` compilation defaults to `androidDebug` and needs two.
        val compiledSourceSets: Map<String, KotlinSourceSet> = compilation.defaultSourceSet
            .withDependsOnClosure()
            .associateBy { it.name }

        GeneratedSourceSet.names.forEach { sourceSetName ->
            val sourceSet = compiledSourceSets[sourceSetName] ?: return@forEach

            val genTask = project.tasks.named(
                sourceSet.getGenerationTaskName(),
                GenerateBitsStringsTask::class.java
            )

            if (isKmpLibrary) {
                // only androidMain generates an android `res` tree; registering the task of any
                // other source set would make agp create and track an always empty directory. The
                // mappings of those source sets reach the compilation through `kotlin.srcDir`.
                if (sourceSetName != GeneratedSourceSet.ANDROID.sourceSetName) return@forEach

                (target as KotlinMultiplatformAndroidLibraryTarget).wireKmpAndroidResources(compilation, genTask)
            } else {
                (target as KotlinAndroidTarget).wireAndroidResources(sourceSet, genTask)
            }
        }
    }

    /**
     * This source set plus every source set reachable through `dependsOn`, transitively. Breadth
     * first from `this`, so the result is ordered and stable; the visited set also guards against
     * the cyclic graph a misconfigured consumer can declare.
     *
     * Hand-rolled rather than using KGP's `withDependsOnClosure`, which is internal API.
     */
    private fun KotlinSourceSet.withDependsOnClosure(): Set<KotlinSourceSet> {
        val visited: MutableSet<KotlinSourceSet> = linkedSetOf()
        val queue = ArrayDeque<KotlinSourceSet>().apply { add(this@withDependsOnClosure) }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (!visited.add(current)) continue
            queue.addAll(current.dependsOn)
        }

        return visited
    }


    /** Umbrella task so that `./gradlew generateBitsStrings` regenerates everything. */
    private fun registerAggregateTask(project: Project) {
        project.tasks.register(AGGREGATE_TASK_NAME) {
            it.group = TASK_GROUP
            it.dependsOn(project.tasks.withType<GenerateBitsStringsTask>())
        }
    }

    /** Makes a gradle sync regenerate the mappings, so the IDE never sees stale code. */
    private fun runGenerationOnGradleSync(project: Project) {
        project.tasks.matching { it.name == IDEA_IMPORT_TASK_NAME }.configureEach {
            it.dependsOn(project.tasks.withType<GenerateBitsStringsTask>())
        }
    }

    /**
     * Legacy AGP path: `com.android.library` / `com.android.application` combined with
     * `androidTarget()`. AGP rejects that combination since 9.0 unless the consumer opts out via
     * `android.builtInKotlin=false` / `android.newDsl=false`.
     */
    private fun KotlinAndroidTarget.wireAndroidResources(
        sourceSet: KotlinSourceSet,
        genTask: TaskProvider<GenerateBitsStringsTask>,
    ) {
        project.getAndroidSourceSetOrNull(sourceSet)
            ?.res
            ?.srcDir(genTask.map { it.androidResourcesDir })

        // The legacy android SourceSet API does not reliably carry the task dependency of a
        // provider, so hang generation off preBuild — every AGP task runs after it, which also
        // keeps android lint from analysing a stale res directory.
        project.tasks
            .matching { it.name == ANDROID_PRE_BUILD_TASK_NAME }
            .configureEach { it.dependsOn(genTask) }
    }

    /**
     * `com.android.kotlin.multiplatform.library` path: the android library variant of a KMP project
     * exposes its generated res through the variant API instead of android source sets.
     * Requires gradle >= 8.10.
     * */
    private fun KotlinMultiplatformAndroidLibraryTarget.wireKmpAndroidResources(
        compilation: KotlinCompilation<*>,
        genTask: TaskProvider<GenerateBitsStringsTask>,
    ) {
        val androidComponents: KotlinMultiplatformAndroidComponentsExtension =
            project.extensions.findByType() ?: throw GradleException(
                "KotlinMultiplatformAndroidComponentsExtension not found " +
                        "in project with 'com.android.kotlin.multiplatform.library'"
            )

        androidComponents.onVariants { variant ->
            if (compilation !is KotlinMultiplatformAndroidCompilation) return@onVariants
            if (variant.name != compilation.componentName) return@onVariants

            variant.sources.res?.addGeneratedSourceDirectory(
                taskProvider = genTask,
                wiredWith = GenerateBitsStringsTask::androidResourcesDir
            )
        }
    }
}
