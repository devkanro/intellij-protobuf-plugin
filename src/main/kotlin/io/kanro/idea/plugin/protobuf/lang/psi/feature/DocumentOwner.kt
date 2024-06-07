package io.kanro.idea.plugin.protobuf.lang.psi.feature

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.psi.prev

interface DocumentOwner : BaseElement {
    fun navigateInfo(): String? {
        return null
    }

    fun document(): String? {
        val document = this.prev<DocumentElement>()
        if (document?.owner != this) return null
        return document.render()
    }

    fun hoverDocument(): String? {
        return document()
    }
}
