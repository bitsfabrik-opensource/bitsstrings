package com.bitsfabrik.bitsstrings.generator.tasks.registration

import com.bitsfabrik.bitsstrings.generator.plugins.extensions.AndroidStringGeneratorPluginExtension
import com.bitsfabrik.bitsstrings.generator.tasks.GenerateAndroidStringsTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File


internal fun registerAndroidGenerateTask(
    project: Project,
    extension: AndroidStringGeneratorPluginExtension
): TaskProvider<GenerateAndroidStringsTask> {
    val generatedDir = File(
        project.layout.buildDirectory.get().asFile,
        "generated/androidstrings"
    )

    return project.tasks.register(
        "generateAndroidStringsMain",
        GenerateAndroidStringsTask::class.java
    ) { generateTask ->

        generateTask.resourcesClassName.set(extension.resourcesClassName)
        generateTask.resourcesPackageName.set(extension.resourcesPackage)

        val stringsIniFile = File(project.projectDir, extension.stringsIniPath.get())
        generateTask.stringsIniFile.set(stringsIniFile)

        generateTask.androidRClassPackage.set(project.getAndroidPackage())

        generateTask.outputSourcesDir.set(File(generatedDir, "src"))
        generateTask.androidResourcesDir.set(File(generatedDir, "res"))

        generateTask.defaultLanguage.set(extension.defaultLang)
    }
}
