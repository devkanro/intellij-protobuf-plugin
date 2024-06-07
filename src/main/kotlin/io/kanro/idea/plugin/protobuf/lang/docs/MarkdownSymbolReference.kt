package io.kanro.idea.plugin.protobuf.lang.docs

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.AddImportInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownShortReferenceLink

class MarkdownSymbolReference(element: MarkdownShortReferenceLink) :
    PsiReferenceBase<MarkdownShortReferenceLink>(element) {
    override fun getRangeInElement(): TextRange {
        val text = element.text
        val start = if (text.startsWith('[')) 1 else 0
        val end = if (text.endsWith(']')) element.textLength - 1 else element.textLength
        return TextRange.create(start, end)
    }

    private object Resolver : ResolveCache.Resolver {
        override fun resolve(
            ref: PsiReference,
            incompleteCode: Boolean,
        ): PsiElement? {
            ref as MarkdownSymbolReference
            val host =
                InjectedLanguageManager.getInstance(ref.element.project).getInjectionHost(ref.element)
                    as? ProtobufElement ?: return null
            val value = ref.rangeInElement.substring(ref.element.text)
            return if (value.startsWith('.')) {
                ProtobufSymbolResolver.resolveAbsolutely(host, QualifiedName.fromDottedString(value), AnyElement)
            } else {
                ProtobufSymbolResolver.resolveRelatively(host, QualifiedName.fromDottedString(value), AnyElement)
            }
        }
    }

    override fun resolve(): PsiElement? {
        return ResolveCache.getInstance(element.project)
            .resolveWithCaching(this, Resolver, false, false)
    }

    override fun getVariants(): Array<Any> {
        val searchName = element.text.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED).trimStart('[')
        val host =
            InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
                as? ProtobufElement ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        getVariantsInCurrentScope(searchName, host, result, addedElements)
        getVariantsInStubIndex(searchName, host, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsInCurrentScope(
        pattern: String,
        host: ProtobufElement,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        val targetName = pattern.substringBeforeLast('.', "")
        val targetScope =
            if (targetName.isEmpty()) {
                QualifiedName.fromComponents()
            } else {
                QualifiedName.fromDottedString(targetName.trimStart('.'))
            }
        if (targetName.startsWith('.')) {
            ProtobufSymbolResolver.collectAbsolute(host, targetScope, AnyElement)
        } else {
            ProtobufSymbolResolver.collectRelatively(host, targetScope, AnyElement)
        }.forEach {
            if (it in elements) return@forEach
            result += lookupFor(it, targetScope) ?: return@forEach
            elements += it
        }
    }

    private fun getVariantsInStubIndex(
        pattern: String,
        host: ProtobufElement,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        if (pattern.contains('.')) return
        val scope = ProtobufRootResolver.searchScope(host)
        val matcher = PlatformPatterns.string().contains(pattern)
        val currentScope = host.parentOfType<ProtobufScope>()?.scope()
        StubIndex.getInstance().getAllKeys(ShortNameIndex.key, host.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(ShortNameIndex.key, it, host.project, scope, ProtobufElement::class.java)
                .asSequence()
        }.forEach {
            if (it in elements) return@forEach
            result += lookupForStub(it, currentScope) ?: return@forEach
            elements += it
        }
    }

    private fun lookupFor(
        element: ProtobufElement,
        scope: QualifiedName,
    ): LookupElement? {
        if (element !is io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupElement) return null
        return if (element is ProtobufPackageName) {
            element.lookup()?.withLookupString(scope.append(element.name).toString())
                ?.withInsertHandler(packageInsertHandler)
        } else {
            element.lookup()?.withLookupString(scope.append(element.name).toString())
        }
    }

    private fun lookupForStub(
        element: ProtobufElement,
        currentScope: QualifiedName?,
    ): LookupElement? {
        if (element !is ProtobufDefinition) return null
        val qualifiedName = element.qualifiedName() ?: return null
        val targetName =
            if (currentScope != null) {
                qualifiedName.removeCommonPrefix(currentScope)
            } else {
                qualifiedName
            }
        return LookupElementBuilder.create(targetName)
            .withLookupString(qualifiedName.lastComponent!!)
            .withPresentableText(qualifiedName.lastComponent!!)
            .withIcon(element.getIcon(false))
            .withTailText("${element.tailText() ?: ""} (${qualifiedName.removeTail(1)})", true)
            .withTypeText(element.type())
            .withInsertHandler(AddImportInsertHandler(element))
    }

    companion object {
        private val packageInsertHandler = SmartInsertHandler(".", 0, true)
    }
}
