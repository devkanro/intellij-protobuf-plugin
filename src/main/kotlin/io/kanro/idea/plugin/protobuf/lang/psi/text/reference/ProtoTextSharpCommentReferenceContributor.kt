package io.kanro.idea.plugin.protobuf.lang.psi.text.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextSharpLineCommentImpl

class ProtoTextSharpCommentReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(ProtoTextSharpLineCommentImpl::class.java)
                .withParent(ProtoTextFile::class.java),
            ProtoTextSharpCommentReferenceProvider(),
        )
    }
}

class ProtoTextSharpCommentReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        val stringValue = element as? ProtoTextSharpLineCommentImpl ?: return PsiReference.EMPTY_ARRAY
        val reference = getReference(stringValue) ?: return PsiReference.EMPTY_ARRAY
        return arrayOf(reference)
    }

    private fun getReference(element: ProtoTextSharpLineCommentImpl): PsiReference? {
        val text = element.text
        if (text.startsWith(ProtoTextFile.PROTOTEXT_HEADER_FILE)) {
            return ProtoTextHeaderFileReference(element)
        }
        if (text.startsWith(ProtoTextFile.PROTOTEXT_HEADER_MESSAGE)) {
            return ProtoTextHeaderMessageReference(element)
        }
        return null
    }
}
