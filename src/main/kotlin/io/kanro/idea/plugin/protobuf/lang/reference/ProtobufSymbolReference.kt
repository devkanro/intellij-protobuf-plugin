package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.absolutely
import io.kanro.idea.plugin.protobuf.lang.psi.lastResolvedPartIndex
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.resolve

class ProtobufSymbolReference(element: ProtobufSymbolName) : PsiReferenceBase<ProtobufSymbolName>(element) {
    override fun resolve(): PsiElement? {
        val typeName = element.parent as? ProtobufTypeName ?: return null
        var resolvedElement = typeName.resolve() ?: return null
        val lastPartIndex = typeName.lastResolvedPartIndex()
        if (lastPartIndex < 0) return null

        val parts = typeName.symbolNameList
        val currentIndex = parts.indexOf(element)
        if (lastPartIndex < currentIndex) return null
        var level = lastPartIndex - currentIndex
        while (level > 0) {
            resolvedElement = resolvedElement.parentOfType<ProtobufScope>() ?: return null
            if (resolvedElement is ProtobufFile) break
            level--
        }
        return resolvedElement
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        val typeName = element.parent as? ProtobufTypeName ?: return arrayOf()
        val filter = when (typeName.parent) {
            is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionNameVariants(element.parentOfType())
            is ProtobufFieldDefinition,
            is ProtobufMapField -> ProtobufSymbolFilters.fieldTypeNameVariants
            is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeNameVariants
            is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeNameVariants
            else -> return arrayOf()
        }
        val parts = typeName.symbolNameList
        val currentIndex = parts.indexOf(element)
        val targetScope = QualifiedName.fromComponents(parts.subList(0, currentIndex).map { it.text })

        return if (typeName.absolutely()) {
            ProtobufSymbolResolver.collectAbsolute(element, targetScope, filter)
                .map { (it as? ProtobufLookupItem)?.lookup() ?: it }
                .toTypedArray()
        } else {
            ProtobufSymbolResolver.collectRelatively(element, targetScope, filter)
                .map { (it as? ProtobufLookupItem)?.lookup() ?: it }
                .toTypedArray()
        }
    }
}
