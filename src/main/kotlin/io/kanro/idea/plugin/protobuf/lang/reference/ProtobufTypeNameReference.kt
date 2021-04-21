package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.completion.AddImportInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.absolutely
import io.kanro.idea.plugin.protobuf.lang.psi.prev
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix

class ProtobufTypeNameReference(
    element: ProtobufTypeName,
    val symbolIndex: Int,
    val child: ProtobufTypeNameReference?
) :
    PsiReferenceBase<ProtobufTypeName>(element) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(ref: PsiReference, incompleteCode: Boolean): PsiElement? {
            ref as ProtobufTypeNameReference
            val symbolNameList = ref.element.symbolNameList
            val resolveName = QualifiedName.fromComponents(
                symbolNameList.subList(0, ref.symbolIndex + 1).mapNotNull { it.identifierLiteral?.text }
            )
            val filter = when (ref.element.parent) {
                is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionNameVariants(ref.element.parentOfType())
                is ProtobufFieldDefinition,
                is ProtobufMapFieldDefinition -> ProtobufSymbolFilters.fieldTypeNameVariants
                is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeNameVariants
                is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeNameVariants
                else -> AnyElement
            }
            return if (ref.element.absolutely()) {
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
        return element.symbolNameList[symbolIndex].textRangeInParent
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val filter = when (element.parent) {
            is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionNameVariants(element.parentOfType())
            is ProtobufFieldDefinition,
            is ProtobufMapFieldDefinition -> ProtobufSymbolFilters.fieldTypeNameVariants
            is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeNameVariants
            is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeNameVariants
            else -> return arrayOf()
        }
        val pattern = element.text
        getVariantsInCurrentScope(pattern, filter, result, addedElements)
        getVariantsInStubIndex(pattern, filter, result, addedElements)
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
        if (element.absolutely()) {
            ProtobufSymbolResolver.collectAbsolute(element, targetScope, filter)
        } else {
            ProtobufSymbolResolver.collectRelatively(element, targetScope, filter)
        }.forEach {
            if (it in elements) return@forEach
            result += lookupFor(it, targetScope) ?: return@forEach
            elements += it
        }
    }

    private fun getVariantsInStubIndex(
        pattern: String,
        filter: PsiElementFilter,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>
    ): Array<Any> {
        if (pattern.contains('.')) return arrayOf()
        if (!pattern.endsWith("IntellijIdeaRulezzz")) return arrayOf()
        val searchName = pattern.substringBefore("IntellijIdeaRulezzz")

        val module = ModuleUtil.findModuleForPsiElement(element)
        val scope = if (module != null) {
            GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
        } else {
            GlobalSearchScope.projectScope(element.project)
        }

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

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.symbolNameList[symbolIndex].identifierLiteral?.node as? LeafElement)
            ?.replaceWithText(newElementName)
        return element
    }

    private fun lookupFor(element: ProtobufElement, scope: QualifiedName): LookupElement? {
        var builder = (element as? ProtobufLookupItem)?.lookup() ?: return null
        builder = builder.withLookupString(scope.append(builder.lookupString).toString())
        if (element is ProtobufPackageName) {
            builder = builder.withInsertHandler(packageInsertHandler)
        }
        return builder
    }

    private fun lookupForStub(element: ProtobufElement, currentScope: QualifiedName?): LookupElement? {
        if (element !is ProtobufScopeItem) return null
        val qualifiedName = element.qualifiedName() ?: return null

        val targetName = if (currentScope != null) {
            qualifiedName.removeCommonPrefix(currentScope)
        } else qualifiedName
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
