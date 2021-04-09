package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufEnumValueReference

abstract class ProtobufEnumValueBase(node: ASTNode) : ProtobufElementBase(node), ProtobufEnumValue {
    override fun getReference(): PsiReference? {
        return ProtobufEnumValueReference(this)
    }
}
