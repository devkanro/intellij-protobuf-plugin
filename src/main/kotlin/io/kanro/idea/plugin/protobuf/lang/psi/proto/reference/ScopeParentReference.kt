package io.kanro.idea.plugin.protobuf.lang.psi.proto.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItem

abstract class ScopeParentReference(
    element: PsiElement,
    private val reference: PsiReference,
) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val parent = reference.resolve() ?: return null
        if (parent is ProtobufScopeItem) {
            return parent.owner()
        }
        return null
    }

    override fun getVariants(): Array<Any> {
        return reference.variants
    }
}
