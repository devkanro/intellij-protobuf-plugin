package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType

interface BooleanValue : ValueElement<Boolean> {
    override fun value(): Boolean = this.text.toBoolean()

    override fun valueType(): ValueType = ValueType.BOOLEAN
}
