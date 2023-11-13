package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHover
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.util.ProtobufPsiFactory

abstract class ProtobufTypeNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufTypeName {
    private val hover =
        object : ProtobufSymbolReferenceHover {
            override fun symbolParts(): List<ProtobufSymbolReferenceHover.SymbolPart> {
                return symbolNameList.map {
                    ProtobufSymbolReferenceHover.SymbolPart(it.startOffsetInParent, it.text)
                }
            }

            override fun textRange(): TextRange {
                return textRange
            }

            override fun renamePart(
                index: Int,
                newName: String,
            ) {
                (symbolNameList[index].identifierLiteral?.node as? LeafElement)?.replaceWithText(newName)
            }

            override fun rename(newName: String) {
                replace(ProtobufPsiFactory.createTypeName(project, newName))
            }

            override fun absolutely(): Boolean {
                return firstChild !is ProtobufSymbolName
            }

            override fun variantFilter(): PsiElementFilter {
                return when (parent) {
                    is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionNameVariants(parentOfType())
                    is ProtobufFieldDefinition,
                    is ProtobufMapFieldDefinition,
                    -> ProtobufSymbolFilters.fieldTypeNameVariants
                    is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeNameVariants
                    is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeNameVariants
                    else -> ProtobufSymbolFilters.alwaysFalse
                }
            }
        }

    override fun referencesHover(): ProtobufSymbolReferenceHover {
        return hover
    }

    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }
}
