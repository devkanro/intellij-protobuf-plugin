package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType

interface EnumValue : ValueElement<String> {
    override fun value(): String = this.text

    override fun valueType(): ValueType = ValueType.ENUM
}
