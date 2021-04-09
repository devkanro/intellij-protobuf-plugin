package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolReference

abstract class ProtobufSymbolNameBase(node: ASTNode) : ProtobufElementBase(node), ProtobufSymbolName {
    override fun getReference(): PsiReference? {
        return ProtobufSymbolReference(this)
    }
}
