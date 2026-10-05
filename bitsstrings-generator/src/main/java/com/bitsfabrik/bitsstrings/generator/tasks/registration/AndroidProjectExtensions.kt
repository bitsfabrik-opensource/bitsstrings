package com.bitsfabrik.bitsstrings.generator.tasks.registration

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.gradle.BaseExtension
import com.bitsfabrik.bitsstrings.generator.domain.AndroidPluginIds
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.sources.android.androidSourceSetInfoOrNull
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * The package the generated `R` class lives in, or `null` for a project without an android target.
 * Resolved lazily — the android extension is not configured yet while the plugin is being applied.
 */
internal fun Project.getAndroidPackage(): Provider<String> {
    return provider {
        // before call android specific classes we should ensure that android plugin is in classpath at all
        if (!isAndroidEnabled()) return@provider null

        if (plugins.hasPlugin(AndroidPluginIds.KMP_LIBRARY)) {
            getKmpAndroidNamespace()
        } else {
            // AGP 8+: ApplicationExtension/LibraryExtension are registered as CommonExtension.
            getAndroidNamespaceFromCommon()
            // Fallback to the legacy BaseExtension
                ?: getAndroidNamespaceFromBase()
                // Manually get Package from Manifest
                ?: getAndroidNamespaceFromManifest()
        }
    }
}

/** The legacy android source set (`main`, `debug`, …) matching a KMP source set, if there is one. */
@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.getAndroidSourceSetOrNull(kotlinSourceSet: KotlinSourceSet): AndroidSourceSet? {
    val androidSourceSetInfo = kotlinSourceSet.androidSourceSetInfoOrNull ?: return null
    val android = extensions.findByType<BaseExtension>() ?: return null
    return android.sourceSets.getByName(androidSourceSetInfo.androidSourceSetName)
}

private fun Project.isAndroidEnabled(): Boolean =
    AndroidPluginIds.all.any { plugins.findPlugin(it) != null }

private fun Project.getAndroidNamespaceFromCommon(): String? =
    extensions.findByType(CommonExtension::class.java)?.namespace

private fun Project.getAndroidNamespaceFromBase(): String? =
    extensions.findByType<BaseExtension>()?.namespace

private fun Project.getAndroidNamespaceFromManifest(): String {
    val manifestFile = extensions.findByType<BaseExtension>()?.getManifestFile()
    val dbFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val dBuilder: DocumentBuilder = dbFactory.newDocumentBuilder()
    val doc: Document = dBuilder.parse(manifestFile)

    val manifestNodes: NodeList = doc.getElementsByTagName("manifest")
    val manifest: Node = manifestNodes.item(0)

    return manifest.attributes.getNamedItem("package").textContent
}

private fun BaseExtension.getManifestFile(): File {
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    return mainSourceSet.manifest.srcFile
}

private fun Project.getKmpAndroidNamespace(): String? {
    val kotlinProjectExtension = extensions.findByType<KotlinProjectExtension>()

    val androidLibraryExtension: KotlinMultiplatformAndroidLibraryExtension? =
        kotlinProjectExtension
            ?.extensions
            ?.findByType<KotlinMultiplatformAndroidLibraryExtension>()

    return androidLibraryExtension?.namespace
}
