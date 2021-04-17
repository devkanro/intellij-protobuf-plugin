package io.kanro.idea.plugin.protobuf.string

/**
 * Returns a character at the given [index] or `0` if the [index] is out of bounds of this char sequence.
 */
fun CharSequence.getOrZero(index: Int): Char {
    return if (index in 0..lastIndex) get(index) else 0.toChar()
}
