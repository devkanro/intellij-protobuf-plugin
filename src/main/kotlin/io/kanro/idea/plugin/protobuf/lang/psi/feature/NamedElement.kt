package io.kanro.idea.plugin.protobuf.lang.psi.feature

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement

interface NamedElement : BaseElement {
    fun name(): String?
}
