package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.prev

interface ProtobufDocumented : PsiElement {
    @JvmDefault
    fun navigateInfo(): String? {
        return null
    }

    @JvmDefault
    fun document(): String? {
        val document = this.prev<ProtobufDocument>()
        if (document?.owner != this) return null
        return document.render()
    }

    @JvmDefault
    fun hoverDocument(): String? {
        return document()
    }
}
