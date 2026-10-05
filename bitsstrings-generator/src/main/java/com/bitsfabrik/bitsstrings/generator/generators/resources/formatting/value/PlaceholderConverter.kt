package com.bitsfabrik.bitsstrings.generator.generators.resources.formatting.value

/*  %[argument_index$][width][.precision]conversion
      conversions:
        d = integer
        f = floating point
        s = string
        @ = string
 */
private val FORMAT_SPECIFIER_REGEX = "%(\\d+\$)?(\\d+)?(\\.\\d+)?([dfs@])".toRegex()

private const val GROUP_WIDTH = 2
private const val GROUP_PRECISION = 3
private const val GROUP_CONVERSION = 4

/**
 * Numbers every format specifier (`%s` → `%1$s`, …) and maps the two interchangeable string
 * conversions — android writes `%s`, apple writes `%@` — to [stringConversion]. Width and
 * precision are kept as authored.
 */
internal fun String.withPositionalPlaceholders(stringConversion: String): String {
    var placeHolderCount = 1
    return FORMAT_SPECIFIER_REGEX.replace(this) { match ->
        val width = match.groupValues[GROUP_WIDTH]
        val precision = match.groupValues[GROUP_PRECISION]
        val conversion = match.groupValues[GROUP_CONVERSION].let { conversion ->
            if (conversion == "s" || conversion == "@") stringConversion else conversion
        }

        "%${placeHolderCount++}\$$width$precision$conversion"
    }
}
