package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufBuiltInOptionReference

abstract class ProtobufBuiltInOptionMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufBuiltInOptionName {
    override fun getReference(): PsiReference {
        return ProtobufBuiltInOptionReference(this)
    }
}
