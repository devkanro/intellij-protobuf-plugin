package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufFieldReference

abstract class ProtobufFieldNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufFieldName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result(
                if (symbolName != null) {
                    arrayOf(ProtobufFieldReference(this))
                } else {
                    emptyArray()
                },
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
    }
}
