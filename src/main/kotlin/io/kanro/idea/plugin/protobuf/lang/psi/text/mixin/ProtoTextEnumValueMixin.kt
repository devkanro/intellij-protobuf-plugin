package io.kanro.idea.plugin.protobuf.lang.psi.text.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextEnumValueReference

abstract class ProtoTextEnumValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtoTextEnumValue {
    override fun getReference(): PsiReference {
        return ProtoTextEnumValueReference(this)
    }
}
