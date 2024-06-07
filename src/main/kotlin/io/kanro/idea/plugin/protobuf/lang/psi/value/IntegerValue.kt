package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface IntegerValue : ValueElement<Int> {
    override fun value(): Int = this.text.toInt()
}
