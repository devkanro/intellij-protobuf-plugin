package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface NumberValue : ValueElement<Number> {
    override fun value(): Number {
        val text = this.text.lowercase()
        return when {
            text == "inf" -> Double.POSITIVE_INFINITY
            text == "-inf" -> Double.NEGATIVE_INFINITY
            text == "nan" -> Double.NaN
            text.contains('e', true) -> this.text.toDouble()
            text.startsWith("0x") -> this.text.substring(2).toLong(16)
            text.startsWith("0") -> this.text.toLong(8)
            this.text.contains('.') -> this.text.toDouble()
            else -> this.text.toLong()
        }
    }
}
