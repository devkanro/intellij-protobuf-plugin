package io.kanro.idea.plugin.protobuf.lang.psi.feature

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.psi.findChild

interface BodyOwner : BaseElement {
    fun body(): BodyElement? {
        return findChild()
    }
}
