package com.bitsfabrik.bitsstrings.generator.testing

import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/** Gives each test a private scratch directory, removed again afterwards. */
internal abstract class TempDirectoryTest {

    protected lateinit var tempRoot: File

    @BeforeTest
    fun createTempRoot() {
        tempRoot = Files.createTempDirectory("bitsstrings-test").toFile()
    }

    @AfterTest
    fun deleteTempRoot() {
        tempRoot.deleteRecursively()
    }

    /** A path below [tempRoot]; not created on disk, the code under test is expected to do that. */
    protected fun dir(name: String) = File(tempRoot, name)

    protected fun File.writeStaleFile(relativePath: String): File =
        File(this, relativePath).apply {
            parentFile.mkdirs()
            writeText("stale content of a key that no longer exists")
        }
}
