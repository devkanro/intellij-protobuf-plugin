package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.isFieldDefaultOption
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufBuiltInOptionReference

abstract class ProtobufBuiltInOptionNameBase(node: ASTNode) : ProtobufElementBase(node), ProtobufBuiltInOptionName {
    override fun getReference(): PsiReference? {
        if (this.isFieldDefaultOption()) return null
        return ProtobufBuiltInOptionReference(this)
    }
}
