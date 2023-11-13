package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceQualifiedNameIndex
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex

class GrpcServiceReference(element: HttpRequestTarget, range: TextRange) :
    PsiReferenceBase<HttpRequestTarget>(element, range) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(
            ref: PsiReference,
            incompleteCode: Boolean,
        ): PsiElement? {
            ref as GrpcServiceReference
            val service = ref.rangeInElement.substring(ref.element.text)
            return StubIndex.getElements(
                QualifiedNameIndex.key,
                service,
                ref.element.project,
                GlobalSearchScope.allScope(ref.element.project),
                ProtobufElement::class.java,
            ).firstOrNull()
        }
    }

    override fun resolve(): PsiElement? {
        return ResolveCache.getInstance(element.project)
            .resolveWithCaching(this, Resolver, false, false)
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val pattern = rangeInElement.substring(element.text)
        getVariantsForShortName(pattern, result, addedElements)
        // getVariantsForQualifiedName(pattern, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsForShortName(
        pattern: String,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        if (pattern.contains('.')) return
        if (!pattern.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) return
        val searchName = pattern.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
        val scope = GlobalSearchScope.allScope(element.project)
        val matcher = PlatformPatterns.string().contains(searchName)
        StubIndex.getInstance().getAllKeys(ServiceShortNameIndex.key, element.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(
                ServiceShortNameIndex.key,
                it,
                element.project,
                scope,
                ProtobufServiceDefinition::class.java,
            )
                .asSequence()
        }.forEach {
            if (it in elements) return@forEach
            result += lookupFor(it) ?: return@forEach
            elements += it
        }
    }

    private fun getVariantsForQualifiedName(
        pattern: String,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        val searchName =
            pattern.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED).substringBeforeLast('.', "").trim('.')
        val scope = GlobalSearchScope.allScope(element.project)
        val matcher = PlatformPatterns.string().startsWith(searchName)
        StubIndex.getInstance().getAllKeys(ServiceQualifiedNameIndex.key, element.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(
                ServiceQualifiedNameIndex.key,
                it,
                element.project,
                scope,
                ProtobufServiceDefinition::class.java,
            )
                .asSequence()
        }.forEach {
            if (it in elements) return@forEach
            result += lookupFor(it) ?: return@forEach
            elements += it
        }
    }

    private fun lookupFor(element: ProtobufServiceDefinition): LookupElement? {
        val qualifiedName = element.qualifiedName() ?: return null
        val name = element.name() ?: return null
        return LookupElementBuilder.create(qualifiedName.toString())
            .withLookupString(name)
            .withPresentableText(name)
            .withIcon(element.getIcon(false))
            .withTailText("${element.tailText() ?: ""} (${qualifiedName.removeTail(1)})", true)
            .withTypeText(element.type())
            .withInsertHandler(SmartInsertHandler("/", autoPopup = true))
    }
}
