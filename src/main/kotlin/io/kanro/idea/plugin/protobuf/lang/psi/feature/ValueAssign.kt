package io.kanro.idea.plugin.protobuf.lang.psi.feature

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.psi.value.WrappedValue

interface ValueAssign : BaseElement, WrappedValue {
    fun field(): NamedElement?
}
