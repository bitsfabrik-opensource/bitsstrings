package com.bitsfabrik.bitsstrings.generator.domain

import com.bitsfabrik.bitsstrings.generator.testing.TempDirectoryTest
import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

internal class IosTableClaimsTest : TempDirectoryTest() {

    private val claims = IosTableClaims()

    @Test
    fun allowsDifferentTablesInTheSameDirectory() {
        claims.claim(dir("resources"), "LoginStrings", ":feature:login")
        claims.claim(dir("resources"), "SharedStrings", ":shared")
    }

    @Test
    fun allowsTheSameTableInDifferentDirectories() {
        claims.claim(dir("app/resources"), "Localizable", ":shared")
        claims.claim(dir("lib/resources"), "Localizable", ":auth")
    }

    @Test
    fun allowsTheSameProjectToClaimItsTableAgain() {
        claims.claim(dir("resources"), "Localizable", ":shared")
        claims.claim(dir("resources"), "Localizable", ":shared")
    }

    @Test
    fun failsWhenTwoProjectsWriteTheSameTableIntoTheSameDirectory() {
        claims.claim(dir("resources"), "Localizable", ":shared")

        val error = assertFailsWith<GradleException> {
            // a different spelling of the same directory still collides
            claims.claim(dir("other/../resources"), "Localizable", ":feature:login")
        }

        assertContains(error.message.orEmpty(), ":shared")
        assertContains(error.message.orEmpty(), ":feature:login")
        assertContains(error.message.orEmpty(), "iosTableName")
    }
}
