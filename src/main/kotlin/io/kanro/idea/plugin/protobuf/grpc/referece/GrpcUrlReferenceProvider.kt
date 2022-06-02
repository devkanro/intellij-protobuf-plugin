package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class GrpcUrlReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element !is HttpRequestTarget) return arrayOf()
        val path = element.pathAbsolute ?: return arrayOf()
        val separatorIndex = path.text.withIndex().asSequence().filter { it.value == '/' }.map { it.index }.toList()
        val pathRange = path.textRangeInParent
        return when (separatorIndex.size) {
            1 -> {
                val serviceRange = TextRange(pathRange.startOffset + separatorIndex[0] + 1, pathRange.endOffset)
                arrayOf(GrpcServiceReference(element, serviceRange))
            }
            2 -> {
                val serviceRange =
                    TextRange(pathRange.startOffset + separatorIndex[0] + 1, pathRange.startOffset + separatorIndex[1])
                val methodRange = TextRange(pathRange.startOffset + separatorIndex[1] + 1, pathRange.endOffset)
                arrayOf(GrpcServiceReference(element, serviceRange), GrpcMethodReference(element, methodRange))
            }
            else -> arrayOf()
        }
    }
}