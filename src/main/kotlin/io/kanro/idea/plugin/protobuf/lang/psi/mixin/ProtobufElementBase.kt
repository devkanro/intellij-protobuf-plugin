package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

abstract class ProtobufElementBase(node: ASTNode) : ASTWrapperPsiElement(node), ProtobufElement {
    override fun getLanguage(): Language {
        return ProtobufLanguage
    }
}
