package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionFieldName
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufOptionFieldNameReference

abstract class ProtobufOptionFieldNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufOptionFieldName {
    override fun getReference(): PsiReference {
        return ProtobufOptionFieldNameReference(this)
    }
}
