package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufFieldReference

abstract class ProtobufFieldNameBase(node: ASTNode) : ProtobufElementBase(node), ProtobufFieldName {
    override fun getReference(): PsiReference? {
        return ProtobufFieldReference(this)
    }
}
