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

fun TextRange.trim(text: String, char: Char): TextRange {
    val subText = substring(text)
    if (subText.trimStart(char).isEmpty()) return TextRange.create(startOffset, startOffset)
    val start = this.startOffset + (subText.length - subText.trimStart(char).length)
    val end = this.endOffset - (subText.length - subText.trimEnd(char).length)
    return TextRange.create(start, end)
}
