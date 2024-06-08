package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufImportReference

abstract class ProtobufImportStatementMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufImportStatement {
    override fun getReference(): PsiReference {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(ProtobufImportReference(this), PsiModificationTracker.MODIFICATION_COUNT)
        }
    }
}
