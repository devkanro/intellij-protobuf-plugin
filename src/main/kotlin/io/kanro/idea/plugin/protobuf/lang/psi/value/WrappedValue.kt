package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.findChild

interface WrappedValue : ValueElement<Any?> {
    override fun value(): Any? = valueElement()?.value()

    fun valueElement(): ValueElement<*>? {
        return this.findChild<ValueElement<*>>()
    }

    override fun valueType(): ValueType = valueElement()?.valueType() ?: ValueType.UNKNOWN
}
