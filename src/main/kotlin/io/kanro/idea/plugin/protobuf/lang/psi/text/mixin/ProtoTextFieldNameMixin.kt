package io.kanro.idea.plugin.protobuf.lang.psi.text.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.HintedReferenceHost
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextFieldReference

abstract class ProtoTextFieldNameMixin(node: ASTNode) :
    ProtoTextElementBase(node),
    ProtoTextFieldName,
    HintedReferenceHost {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        symbolName?.let {
            return arrayOf(ProtoTextFieldReference(this))
        }

        extensionName?.let {
            return emptyArray()
        }

        anyName?.let {
            return emptyArray()
        }

        return emptyArray()
    }

    override fun getReferences(hints: PsiReferenceService.Hints): Array<PsiReference> {
        return references
    }

    override fun shouldAskParentForReferences(hints: PsiReferenceService.Hints): Boolean {
        return false
    }
}
