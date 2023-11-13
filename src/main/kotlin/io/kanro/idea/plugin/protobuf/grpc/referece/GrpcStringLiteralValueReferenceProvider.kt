package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class GrpcStringLiteralValueReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        if (element !is JsonStringLiteral) return PsiReference.EMPTY_ARRAY
        val property = element.parent as? JsonProperty ?: return PsiReference.EMPTY_ARRAY

        return if (property.name == "@type") {
            arrayOf(GrpcTypeUrlReference(element))
        } else {
            arrayOf(GrpcMessageEnumValueReference(element))
        }
    }
}
