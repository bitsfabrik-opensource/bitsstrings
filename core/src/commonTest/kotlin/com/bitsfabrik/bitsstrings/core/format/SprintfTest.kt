package com.bitsfabrik.bitsstrings.core.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SprintfTest {

    @Test
    fun keepsTextWithoutSpecifications() {
        assertEquals("hello world", "hello world".sprintf())
    }

    @Test
    fun writesLiteralPercent() {
        assertEquals("100% sure", "100%% sure".sprintf())
        assertEquals("50%", "%d%%".sprintf(50))
    }

    @Test
    fun formatsText() {
        assertEquals("hello world", "hello %s".sprintf("world"))
        assertEquals("1 and true", "%s and %s".sprintf(1, true))
        assertEquals("null", "%s".sprintf(null))

        assertEquals("hello world", "hello %@".sprintf("world"))
        assertEquals("1 and true", "%@ and %@".sprintf(1, true))
        assertEquals("null", "%@".sprintf(null))
    }

    @Test
    fun cutsTextToPrecision() {
        assertEquals("abc", "%.3s".sprintf("abcdef"))
        assertEquals("ab", "%.5s".sprintf("ab"))
    }

    @Test
    fun formatsIntegers() {
        assertEquals("42", "%d".sprintf(42))
        assertEquals("-42", "%i".sprintf(-42))
        assertEquals("+42", "%+d".sprintf(42))
        assertEquals("+0", "%+d".sprintf(0))
        assertEquals("7", "%d".sprintf(7.9))
        assertEquals("9223372036854775807", "%d".sprintf(Long.MAX_VALUE))
    }

    @Test
    fun formatsFloats() {
        assertEquals("1.500000", "%f".sprintf(1.5))
        assertEquals("3.14", "%.2f".sprintf(3.14159))
        assertEquals("-3.14", "%.2f".sprintf(-3.14159))
        assertEquals("2.00", "%.2f".sprintf(2))
        assertEquals("0.0", "%.1f".sprintf(0.0))
    }

    @Test
    fun roundsFloats() {
        assertEquals("3", "%.0f".sprintf(2.5))
        assertEquals("10.00", "%.2f".sprintf(9.999))
        assertEquals("0.1", "%.1f".sprintf(0.05))
        assertEquals("1.00", "%.2f".sprintf(1.005))
        assertEquals("0.30", "%.2f".sprintf(0.1 + 0.2))
        assertEquals("-0.01", "%.2f".sprintf(-0.005))
    }

    @Test
    fun padsToWidth() {
        assertEquals("|   abc|", "|%6s|".sprintf("abc"))
        assertEquals("|abc   |", "|%-6s|".sprintf("abc"))
        assertEquals("| abc  |", "|%^6s|".sprintf("abc"))
        assertEquals("|    42|", "|%6d|".sprintf(42))
        assertEquals("|  3.14|", "|%6.2f|".sprintf(3.14159))
        assertEquals("|abcdef|", "|%3s|".sprintf("abcdef"))
    }

    @Test
    fun padsWithFillCharacter() {
        assertEquals("00042", "%05d".sprintf(42))
        assertEquals("***42", "%*5d".sprintf(42))
        assertEquals("__abc", "%_5s".sprintf("abc"))
        assertEquals("0003.14", "%07.2f".sprintf(3.14159))
    }

    @Test
    fun keepsSignInFrontOfZeroPadding() {
        assertEquals("-0042", "%05d".sprintf(-42))
        assertEquals("+0042", "%+05d".sprintf(42))
        assertEquals("-003.14", "%07.2f".sprintf(-3.14159))
    }

    @Test
    fun usesExplicitArgumentIndex() {
        assertEquals("b a", "%2\$s %1\$s".sprintf("a", "b"))
        assertEquals("x-x", "%1\$s-%1\$s".sprintf("x"))
        assertEquals("|ab    |", "|%1\$-6s|".sprintf("ab"))
        assertEquals("|  3.14|", "|%1\$6.2f|".sprintf(3.14159))
        assertEquals("|  3.14|", "|%10\$6.2f|".sprintf(1, 2, 3, 4, 5, 6, 7, 8, 9, 3.14159))
    }

    @Test
    fun explicitIndexDoesNotShiftTheImplicitOrder() {
        assertEquals("a a b", "%s %1\$s %s".sprintf("a", "b"))
    }

    @Test
    fun rejectsBrokenFormats() {
        assertFailsWith<IllegalArgumentException> { "%q".sprintf(1) }
        assertFailsWith<IllegalArgumentException> { "%d".sprintf("no number") }
        assertFailsWith<IllegalArgumentException> { "%s".sprintf() }
        assertFailsWith<IllegalArgumentException> { "%2\$s".sprintf("only one") }
        assertFailsWith<IllegalArgumentException> { "unterminated %".sprintf() }
        assertFailsWith<IllegalArgumentException> { "%.2.3f".sprintf(1.0) }
        assertFailsWith<IllegalArgumentException> { "%5-d".sprintf(1) }
    }
}
