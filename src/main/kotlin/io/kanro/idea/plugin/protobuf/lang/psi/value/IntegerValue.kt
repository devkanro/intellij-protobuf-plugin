package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType

interface IntegerValue : ValueElement<Int> {
    override fun value(): Int = this.text.toInt()

    override fun valueType(): ValueType = ValueType.NUMBER
}
