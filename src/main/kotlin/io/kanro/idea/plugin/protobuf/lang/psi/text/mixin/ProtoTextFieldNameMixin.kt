package io.kanro.idea.plugin.protobuf.lang.psi.text.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextFieldReference

abstract class ProtoTextFieldNameMixin(node: ASTNode) :
    ProtoTextElementBase(node),
    ProtoTextFieldName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result(
                if (symbolName != null) {
                    arrayOf(ProtoTextFieldReference(this))
                } else {
                    emptyArray()
                },
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
    }
}
