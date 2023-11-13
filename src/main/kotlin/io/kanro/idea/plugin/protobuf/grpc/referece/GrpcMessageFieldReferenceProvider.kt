package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class GrpcMessageFieldReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        if (element !is JsonStringLiteral) return PsiReference.EMPTY_ARRAY
        return arrayOf(GrpcMessageFieldReference(element))
    }
}
