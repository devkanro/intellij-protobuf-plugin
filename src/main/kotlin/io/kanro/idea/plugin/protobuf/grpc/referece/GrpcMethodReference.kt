package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceMethodIndex
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceQualifiedNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class GrpcMethodReference(element: HttpRequestTarget, range: TextRange) :
    PsiReferenceBase<HttpRequestTarget>(element, range) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(
            ref: PsiReference,
            incompleteCode: Boolean,
        ): PsiElement? {
            ref as GrpcMethodReference
            val methodName = ref.element.pathAbsolute?.text?.trim('/') ?: return null
            return StubIndex.getElements(
                ServiceMethodIndex.key,
                methodName,
                ref.element.project,
                GlobalSearchScope.allScope(ref.element.project),
                ProtobufRpcDefinition::class.java,
            ).firstOrNull()
        }
    }

    override fun resolve(): PsiElement? {
        return ResolveCache.getInstance(element.project)
            .resolveWithCaching(this, Resolver, false, false)
    }

    override fun getVariants(): Array<Any> {
        val text = element.text
        val pattern = rangeInElement.substring(text)
        val start = text.lastIndexOf('/', rangeInElement.startOffset - 2)
        val service = TextRange(start + 1, rangeInElement.startOffset - 1).substring(text)

        val serviceDefinition =
            StubIndex.getElements(
                ServiceQualifiedNameIndex.key,
                service,
                element.project,
                GlobalSearchScope.allScope(element.project),
                ProtobufServiceDefinition::class.java,
            ).firstOrNull() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY

        val methods = serviceDefinition.items().filterIsInstance<ProtobufRpcDefinition>()
        return methods.mapNotNull { it.lookup() }.toTypedArray()
    }
}
