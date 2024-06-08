package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufReservedRange.range(): LongRange? {
    val numbers = integerValueList.map { it.text.toLong() }
    return when (numbers.size) {
        1 ->
            if (lastChild.textMatches("max")) {
                LongRange(numbers[0], Long.MAX_VALUE)
            } else {
                LongRange(numbers[0], numbers[0])
            }

        2 -> LongRange(numbers[0], numbers[1])
        else -> null
    }
}
