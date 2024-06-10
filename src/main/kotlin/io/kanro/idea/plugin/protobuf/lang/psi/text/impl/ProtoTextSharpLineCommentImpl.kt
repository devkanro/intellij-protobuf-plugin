package io.kanro.idea.plugin.protobuf.lang.psi.text.impl

import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElement

class ProtoTextSharpLineCommentImpl(type: IElementType, text: CharSequence) :
    PsiCommentImpl(type, text), ProtoTextElement {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }

    override fun toString(): String {
        return "ProtoTextSharpLineComment($elementType)"
    }
}
