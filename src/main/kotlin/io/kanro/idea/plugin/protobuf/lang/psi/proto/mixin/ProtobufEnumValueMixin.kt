package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufEnumValueReference

abstract class ProtobufEnumValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufEnumValue {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return arrayOf(ProtobufEnumValueReference(this))
    }
}
