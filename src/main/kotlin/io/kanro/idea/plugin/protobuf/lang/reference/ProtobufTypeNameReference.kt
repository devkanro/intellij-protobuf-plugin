package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
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
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement

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
        val filter = when (element.parent) {
            is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionNameVariants(element.parentOfType())
            is ProtobufFieldDefinition,
            is ProtobufMapFieldDefinition -> ProtobufSymbolFilters.fieldTypeNameVariants
            is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeNameVariants
            is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeNameVariants
            else -> return arrayOf()
        }

        val targetName = element.text.substringBeforeLast('.', "").trim('.')
        val targetScope =
            if (targetName.isEmpty()) QualifiedName.fromComponents() else QualifiedName.fromDottedString(targetName)
        return if (element.absolutely()) {
            ProtobufSymbolResolver.collectAbsolute(element, targetScope, filter)
                .mapNotNull { lookupFor(it, targetScope) }
                .toTypedArray()
        } else {
            ProtobufSymbolResolver.collectRelatively(element, targetScope, filter)
                .mapNotNull { lookupFor(it, targetScope) }
                .toTypedArray()
        }
    }

    private fun lookupFor(element: ProtobufElement, scope: QualifiedName): LookupElement? {
        var builder = (element as? ProtobufLookupItem)?.lookup() ?: return null
        builder = builder.withLookupString(scope.append(builder.lookupString).toString())
        if (element is ProtobufPackageName) {
            builder = builder.withInsertHandler(packageInsertHandler)
        }
        return builder
    }

    companion object {
        private val packageInsertHandler = DeclarativeInsertHandler.Builder()
            .insertOrMove(".")
            .triggerAutoPopup()
            .build()
    }
}
