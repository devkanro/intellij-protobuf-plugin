package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufImportReference

abstract class ProtobufImportBase(node: ASTNode) : ProtobufElementBase(node), ProtobufImportStatement {
    override fun getReference(): PsiReference? {
        return ProtobufImportReference(this)
    }
}
