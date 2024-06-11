package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.AddImportInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stringRangeInParent
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix

class AipTypeNameReference(element: ProtobufStringValue) : PsiReferenceBase<ProtobufStringValue>(element) {
    override fun resolve(): PsiElement? {
        val typeName = QualifiedName.fromDottedString(element.value())
        return ProtobufSymbolResolver.resolveRelatively(element, typeName, ProtobufSymbolFilters.message)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.stringRangeInParent()
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val filter = ProtobufSymbolFilters.messageTypeName

        getVariantsInCurrentScope(filter, result, addedElements)
        getVariantsInStubIndex(filter, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsInCurrentScope(
        filter: PsiElementFilter,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        val parent = element.value().substringBeforeLast('.', "").split('.').filter { it.isNotEmpty() }
        val parentScope = QualifiedName.fromComponents(parent)
        ProtobufSymbolResolver.collectRelatively(element, parentScope, filter).forEach {
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
        val value = element.value()
        if (value.contains('.')) return ArrayUtilRt.EMPTY_OBJECT_ARRAY

        val searchName = value.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
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
        val lookupElement = element as? LookupableElement ?: return null
        val fullName = scope.append(lookupElement.name).toString()
        var builder = lookupElement.lookup(fullName) ?: return null
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

    override fun handleElementRename(newElementName: String): PsiElement {
        val scope = element.value().substringBeforeLast('.', "")
        val name =
            if (scope.isEmpty()) {
                newElementName
            } else {
                "$scope.$newElementName"
            }
        return element.replace(ProtobufPsiFactory.createStringValue(element.project, name))
    }

    companion object {
        private val stringValueInsertHandler = SmartInsertHandler("\"")

        private val packageInsertHandler = SmartInsertHandler(".", 0, true)
    }
}
