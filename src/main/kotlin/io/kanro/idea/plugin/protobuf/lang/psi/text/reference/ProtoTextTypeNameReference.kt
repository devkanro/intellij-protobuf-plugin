package io.kanro.idea.plugin.protobuf.lang.psi.text.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.AddImportInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.prev
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.QualifiedNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextAnyName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextExtensionName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.text.scope
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix

class ProtoTextTypeNameReference(typeName: ProtoTextTypeName) : PsiReferenceBase<ProtoTextTypeName>(typeName) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(
            ref: PsiReference,
            incompleteCode: Boolean,
        ): PsiElement? {
            ref as ProtoTextTypeNameReference
            val qualifiedName = ref.element.leaf().symbol() ?: return null

            return when (ref.element.scope()) {
                is ProtoTextAnyName -> {
                     StubIndex.getElements(
                        QualifiedNameIndex.key,
                        qualifiedName.toString(),
                        ref.element.project,
                        GlobalSearchScope.allScope(ref.element.project),
                        ProtobufElement::class.java,
                    ).firstOrNull { it is ProtobufMessageDefinition } as? ProtobufMessageDefinition
                }

                is ProtoTextExtensionName -> {
                    val scope = ref.element.schemaFile() ?: return null
                    ProtobufSymbolResolver.resolveAbsolutely(scope, qualifiedName, ProtobufSymbolFilters.extensionField)
                }

                else -> null
            }
        }
    }

    override fun resolve(): PsiElement? {
        element.typeName?.let {
            return when (val item = it.reference?.resolve()) {
                is ProtobufScopeItem -> {
                    when (val owner = item.owner()) {
                        is ProtobufFile -> owner.packageParts().lastOrNull()
                        else -> owner
                    }
                }
                is ProtobufPackageName -> item.prev<ProtobufPackageName>()
                else -> null
            }
        }

        return ResolveCache.getInstance(element.project).resolveWithCaching(this, Resolver, false, false)
    }

    override fun getCanonicalText(): String {
        return element.text
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.symbolName.textRangeInParent
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.symbolName.identifierLiteral as? LeafPsiElement)?.replaceWithText(newElementName)
        return element
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val filter =
            when (val scope = element.scope()) {
                is ProtoTextAnyName -> ProtobufSymbolFilters.messageTypeName
                is ProtoTextExtensionName -> ProtobufSymbolFilters.extensionFieldQualifiedName
                else -> return ArrayUtilRt.EMPTY_OBJECT_ARRAY
            }

        getVariantsInCurrentScope(filter, result, addedElements)
        getVariantsInStubIndex(filter, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsInCurrentScope(
        filter: PsiElementFilter,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        val parentScope = element.symbol() ?: return
        val scopeElement = element.schemaFile() ?: return
        ProtobufSymbolResolver.collectAbsolute(scopeElement, parentScope, filter).forEach {
            if (it in elements) return@forEach
            result += lookupFor(it, parentScope) ?: return@forEach
            elements += it
        }
    }

    private fun getVariantsInStubIndex(
        filter: PsiElementFilter,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ): Array<Any> {
        if (element.parent is ProtoTextTypeName) return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        if (element.typeName != null) return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        if (!element.text.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) return ArrayUtilRt.EMPTY_OBJECT_ARRAY

        val searchName = element.text.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
        val scope = ProtobufRootResolver.searchScope(element)
        val matcher = PlatformPatterns.string().contains(searchName)
        val currentScope = element.parentOfType<ProtobufScope>()?.scope()

        return StubIndex.getInstance().getAllKeys(ShortNameIndex.key, element.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(ShortNameIndex.key, it, element.project, scope, ProtobufElement::class.java)
                .asSequence()
        }.filter {
            filter.isAccepted(it)
        }.mapNotNull {
            if (it in elements) return@mapNotNull null
            result += lookupForStub(it, currentScope) ?: return@mapNotNull null
            elements += it
        }.toList().toTypedArray()
    }

    private fun lookupFor(
        element: ProtobufElement,
        scope: QualifiedName,
    ): LookupElement? {
        var builder =
            (element as? LookupableElement)?.lookup() ?: return null
        builder = builder.withLookupString(scope.append(builder.lookupString).toString())
        if (element is ProtobufPackageName) {
            builder = builder.withInsertHandler(packageInsertHandler)
        }
        return builder
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
        return LookupElementBuilder.create(targetName).withLookupString(qualifiedName.lastComponent!!)
            .withPresentableText(qualifiedName.lastComponent!!).withIcon(element.getIcon(false))
            .withTailText("${element.tailText() ?: ""} (${qualifiedName.removeTail(1)})", true)
            .withTypeText(element.type()).withInsertHandler(AddImportInsertHandler(element))
    }

    companion object {
        private val packageInsertHandler = SmartInsertHandler(".", 0, true)
    }
}
