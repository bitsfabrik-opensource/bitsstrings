package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value

import kotlin.test.Test
import kotlin.test.assertEquals

class ValueFormatterTest {


    @Test
    fun testFormatValueOrdinary1() {
        testFormatValueUnchanged("Ok")
    }

    @Test
    fun testFormatValueOrdinary2() {
        testFormatValueUnchanged("Ναι")
    }

    @Test
    fun testFormatValueOrdinary3() {
        testFormatValueUnchanged("Úspěšně")
    }

    @Test
    fun testFormatValueOrdinary4() {
        testFormatValueUnchanged("Es ist ein Fehler aufgetreten, bitte versuche es später erneut.")
    }

    @Test
    fun testFormatValueOrdinary5() {
        testFormatValueUnchanged("Παρουσιάστηκε σφάλμα, παρακαλούμε ξαναδοκιμάστε αργότερα.")
    }

    @Test
    fun testFormatValueOrdinary6() {
        testFormatValueUnchanged("8 bis 19 Zeichen")
    }

    @Test
    fun testFormatValueOrdinary7() {
        testFormatValueUnchanged("The following special characters are allowed: A-Z a-z 0-9 ! $ % - . _ @")
    }

    @Test
    fun testFormatValuePlaceholder1() {
        val input = "Login mit %@"
        val expectedAndroidOutput = "Login mit %1\$s"
        val expectedIosOutput = "Login mit %1\$@"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValuePlaceholder2() {
        val input = "Login mit %s"
        val expectedAndroidOutput = "Login mit %1\$s"
        val expectedIosOutput = "Login mit %1\$@"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValuePlaceholder3() {
        val input = "Possible placeholders %s, %d, %f, %@"
        val expectedAndroidOutput = "Possible placeholders %1\$s, %2\$d, %3\$f, %4\$s"
        val expectedIosOutput = "Possible placeholders %1\$@, %2\$d, %3\$f, %4\$@"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValuePlaceholder4() {
        val input = "%s%s%d%f%s%@"
        val expectedAndroidOutput = "%1\$s%2\$s%3\$d%4\$f%5\$s%6\$s"
        val expectedIosOutput = "%1\$@%2\$@%3\$d%4\$f%5\$@%6\$@"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValuePlaceholder5() {
        val input = "this is number placeholder %8d"
        val expectedAndroidOutput = "this is number placeholder %1\$8d"
        val expectedIosOutput = "this is number placeholder %1\$8d"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValuePlaceholder6() {
        val input = "this is floating number placeholder %8.2f"
        val expectedAndroidOutput = "this is floating number placeholder %1\$8.2f"
        val expectedIosOutput = "this is floating number placeholder %1\$8.2f"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValueEscapeSequences() {
        val input = "& ' \""
        val expectedAndroidOutput = "&amp; \\' \""
        val expectedIosOutput = "& ' \""
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValueEscapeLessThan() {
        val input = "<"
        val expectedAndroidOutput = "&lt;"
        val expectedIosOutput = "<"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValueEscapeGreaterThan() {
        val input = ">"
        val expectedAndroidOutput = "&gt;"
        val expectedIosOutput = ">"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValueEscapeAllSpecialCharacters() {
        val input = "& ' \" < >"
        val expectedAndroidOutput = "&amp; \\' \" &lt; &gt;"
        val expectedIosOutput = "& ' \" < >"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    @Test
    fun testFormatValueHtml() {
        val input = "<p>Es wurde eine E-Mail an <a href=\"mailto\">mail@test.at</a> gesendet.</p>"
        val expectedAndroidOutput = "<p>Es wurde eine E-Mail an <a href=\"mailto\">mail@test.at</a> gesendet.</p>"
        val expectedIosOutput = "<p>Es wurde eine E-Mail an <a href=\\\"mailto\\\">mail@test.at</a> gesendet.</p>"
        testFormatValue(input, expectedAndroidOutput, expectedIosOutput)
    }

    private fun testFormatValueUnchanged(input: String) {
        testFormatValue(input, input, input)
    }

    private fun testFormatValue(input: String, expectedAndroidOutput: String, expectedIosOutput: String) {
        val formattedAndroidValue = AndroidValueFormatter.formatValue(input)
        val formattedIosValue = IosValueFormatter.formatValue(input)

        assertEquals(expectedAndroidOutput, formattedAndroidValue)
        assertEquals(expectedIosOutput, formattedIosValue)
    }
}