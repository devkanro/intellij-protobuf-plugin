package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.prev
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName

class ProtobufTypeNameInStringReference(
    element: ProtobufStringValue,
    val textRange: TextRange,
    val child: ProtobufTypeNameInStringReference?
) : PsiReferenceBase<ProtobufStringValue>(element) {

    private object Resolver : ResolveCache.Resolver {
        override fun resolve(ref: PsiReference, incompleteCode: Boolean): PsiElement? {
            ref as ProtobufTypeNameInStringReference
            val value = ref.element.text ?: return null
            val resolveName = value.substring(0, ref.textRange.endOffset).trim('.', '"').toQualifiedName()
            val filter = ProtobufSymbolFilters.rpcTypeNameVariants
            return if (value.startsWith('.')) {
                ProtobufSymbolResolver.resolveAbsolutely(ref.element, resolveName, filter)
            } else {
                ProtobufSymbolResolver.resolveRelatively(ref.element, resolveName, filter)
            }
        }
    }

    override fun resolve(): PsiElement? {
        when (val childResult = child?.resolve()) {
            is ProtobufScopeItem -> {
                val owner = childResult.owner()
                if (owner is ProtobufFile) {
                    return owner.packageParts().last()
                }
                return owner
            }
            is ProtobufPackageName -> return childResult.prev<ProtobufPackageName>()
        }

        return ResolveCache.getInstance(element.project)
            .resolveWithCaching(this, Resolver, false, false)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return textRange
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val filter = ProtobufSymbolFilters.rpcTypeNameVariants
        val pattern = element.value() ?: return arrayOf()
        getVariantsInCurrentScope(pattern, filter, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsInCurrentScope(
        pattern: String,
        filter: PsiElementFilter,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>
    ) {
        val targetName = pattern.substringBeforeLast('.', "").trim('.')
        val targetScope = if (targetName.isEmpty())
            QualifiedName.fromComponents()
        else
            QualifiedName.fromDottedString(targetName)
        if (pattern.startsWith('.')) {
            ProtobufSymbolResolver.collectAbsolute(element, targetScope, filter)
        } else {
            ProtobufSymbolResolver.collectRelatively(element, targetScope, filter)
        }.forEach {
            if (it in elements) return@forEach
            result += lookupFor(it, targetScope) ?: return@forEach
            elements += it
        }
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val leaf = (element.stringLiteral.node as LeafElement)
        val value = element.text
        val start = value.substring(0, textRange.startOffset)
        val end = value.substring(textRange.endOffset, value.length)
        leaf.replaceWithText("$start$newElementName$end")
        return element
    }

    private fun lookupFor(element: ProtobufElement, scope: QualifiedName): LookupElement? {
        var builder = (element as? ProtobufLookupItem)?.lookup() ?: return null
        builder = builder.withLookupString(scope.append(builder.lookupString).toString())
        if (element is ProtobufPackageName) {
            builder = builder.withInsertHandler(packageInsertHandler)
        } else {
            builder = builder.withInsertHandler(insertHandler)
        }
        return builder
    }

    companion object {
        private val packageInsertHandler = SmartInsertHandler(".", 0, true)
        private val insertHandler = SmartInsertHandler("\"")
    }
}
