package com.bitsfabrik.bitsstrings.generator.tasks

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.iniparser.IniParser
import com.bitsfabrik.bitsstrings.generator.iniparser.StringIniParsingError
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

/** The inputs and outputs every string generation task shares, regardless of target platform. */
abstract class AbstractGenerateStringsTask : DefaultTask() {

    @get:InputFiles
    val stringsIniFile: RegularFileProperty = project.objects.fileProperty()

    @get:Input
    abstract val resourcesPackageName: Property<String>

    @get:Input
    abstract val resourcesClassName: Property<String>

    @get:Input
    abstract val defaultLanguage: Property<String>

    /** Generated kotlin mappings. */
    @get:OutputDirectory
    abstract val outputSourcesDir: DirectoryProperty

    /**
     * Generated android `res` tree. Only set for a source set that actually generates android
     * resources — gradle materializes every output directory before running the task, so setting it
     * unconditionally would leave an empty `res` directory next to every other source set.
     */
    @get:Optional
    @get:OutputDirectory
    abstract val androidResourcesDir: DirectoryProperty

    /**
     * The parsed `strings.ini`, or `null` if the configured file does not exist — a module without
     * strings is not an error, it simply has nothing to generate.
     */
    internal fun AbstractGenerateStringsTask.parseStringsIniOrNull(): BitsStringFile? {
        val iniFile = stringsIniFile.asFile.get()

        if (!iniFile.exists()) {
            logger.warn("Skipped '$name': strings.ini file does not exist: ${iniFile.path}")
            return null
        }

        try {
            return IniParser(iniFile).read()
        } catch (ex: StringIniParsingError) {
            throw GradleException("Failed to read strings.ini (${ex.message})")
        }
    }
}


