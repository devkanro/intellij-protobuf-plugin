package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.findChild

interface WrappedValue : ValueElement<Any?> {
    override fun value(): Any? = this.findChild<ValueElement<*>>()?.value()
}
