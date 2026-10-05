package com.bitsfabrik.bitsstrings.generator.iniparser

import com.bitsfabrik.bitsstrings.generator.domain.BitsStringDefinition
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringFile
import com.bitsfabrik.bitsstrings.generator.domain.BitsStringSection
import java.io.File
import java.io.IOException

/**
 * Reads a `strings.ini` line by line. Every non-blank line is one of four definitions:
 *
 * ```
 * [[Section]]        section
 *   [string_key]     string
 *     <one>          plural variation, opens a quantity for the translations below
 *     de = …         translation of the current string / quantity
 * ```
 */
internal class IniParser(private val inputFile: File) {

    companion object {
        private const val UPPER_CAMEL_CASE = "([A-Z][a-z0-9]+)((\\d)|([A-Z0-9][a-z0-9]*))*([A-Z])?"

        private val sectionRegex = "^\\[\\[($UPPER_CAMEL_CASE)\\]\\]$".toRegex()
        private val stringRegex = "^\\[([^\\[\\]\\s]*)\\]$".toRegex()
        private val variationRegex = "<(zero|one|two|few|many|other)>".toRegex()
        private val translationRegex = "^([^=]+)=(.*)$".toRegex()
    }

    /** Parser state, advanced by [parseLine] and reset per [read]. */
    private var currentSection: BitsStringSection.Builder? = null
    private var currentDefinition: BitsStringDefinition.Builder? = null
    private val allKeys: MutableSet<String> = mutableSetOf()

    fun read(): BitsStringFile {
        currentSection = null
        currentDefinition = null
        allKeys.clear()

        return try {
            val file = BitsStringFile.Builder()

            inputFile.bufferedReader().use { reader ->
                var lineNumber = 0
                reader.forEachLine { line ->
                    lineNumber++
                    parseLine(line.trim(), lineNumber, file)
                }
            }

            file.build()
        } catch (e: IOException) {
            throw StringIniParsingError("File does not exist: ${inputFile.absolutePath}")
        }
    }

    private fun parseLine(line: String, lineNumber: Int, file: BitsStringFile.Builder) {
        if (line.isEmpty()) return

        sectionRegex.firstGroupOrNull(line)?.let { title ->
            currentSection = BitsStringSection.Builder(title).also { file.addSection(it) }
            currentDefinition = null
            return
        }

        stringRegex.firstGroupOrNull(line)?.let { key ->
            val section = currentSection
                ?: fail("No Section Definition before String Definition", line, lineNumber)
            currentDefinition = startDefinition(key, section)
            return
        }

        variationRegex.firstGroupOrNull(line)?.let { quantity ->
            val definition = currentDefinition
                ?: fail("No String Definition before Variation Definition", line, lineNumber)
            definition.startQuantity(quantity)
            return
        }

        translationRegex.matchEntire(line)?.let { match ->
            val definition = currentDefinition
                ?: fail("No String Definition before Translation Definition", line, lineNumber)
            definition.addTranslation(
                lang = match.groupValues[1].trim(),
                value = match.groupValues[2].trim()
            )
            return
        }

        throw StringIniParsingError("Error at parsing line #$lineNumber, \"$line\"")
    }

    private fun startDefinition(
        key: String,
        section: BitsStringSection.Builder
    ): BitsStringDefinition.Builder {
        if (!allKeys.add(key)) {
            throw StringIniParsingError("Duplicated string definition: $key")
        }

        return BitsStringDefinition.Builder(key).also { section.addDefinition(it) }
    }

    private fun fail(reason: String, line: String, lineNumber: Int): Nothing =
        throw StringIniParsingError("$reason in line #$lineNumber, \"$line\"")

    /** The first capture group if this regex matches [line] as a whole, `null` otherwise. */
    private fun Regex.firstGroupOrNull(line: String): String? =
        matchEntire(line)?.groupValues?.get(1)?.trim()
}
