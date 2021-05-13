package io.kanro.idea.plugin.protobuf.string

import com.intellij.openapi.util.TextRange

/**
 * Returns a character at the given [index] or `0` if the [index] is out of bounds of this char sequence.
 */
fun CharSequence.getOrZero(index: Int): Char {
    return if (index in 0..lastIndex) get(index) else 0.toChar()
}

fun CharSequence.splitToRange(char: Char, skipEmpty: Boolean = false): List<TextRange> {
    var start = 0
    var end = 0
    val result = mutableListOf<TextRange>()
    forEachIndexed { index, c ->
        if (c == char) {
            if (!skipEmpty || start != end) {
                result += TextRange.create(start, end)
            }
            start = index + 1
            end = index + 1
        } else {
            end++
        }
    }

    if (!skipEmpty || start < length) {
        result += TextRange.create(start, end)
    }
    return result
}

fun String.parseLongOrNull(): Long? {
    if (startsWith("0x", true)) {
        return substring(2).toLongOrNull(16)
    }
    if (startsWith("0") && length > 1 && matches("[0-7]+".toRegex())) {
        return substring(1).toLongOrNull(8)
    }
    return toLongOrNull()
}

fun String.parseDoubleOrNull(): Double? {
    return when (replace("\\s".toRegex(), "")) {
        "nan", "-nan" -> Double.NaN
        "inf" -> Double.POSITIVE_INFINITY
        "-inf" -> Double.NEGATIVE_INFINITY
        else -> toDoubleOrNull()
    }
}
