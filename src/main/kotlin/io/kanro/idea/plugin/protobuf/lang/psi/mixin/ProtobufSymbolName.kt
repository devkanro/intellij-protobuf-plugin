package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufBuiltInOptionReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufEnumValueReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufFieldReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufImportReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufTypeNameReference
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

abstract class ProtobufTypeNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufTypeName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        if (BuiltInType.isBuiltInType(this.text)) {
            return arrayOf()
        }
        return CachedValuesManager.getCachedValue(this) {
            val symbolList = this.symbolNameList
            var reference: ProtobufTypeNameReference? = null
            val result = symbolList.reversed().mapIndexed { index, name ->
                ProtobufTypeNameReference(this, symbolList.size - 1 - index, reference).apply {
                    reference = this
                }
            }.toTypedArray<PsiReference>()
            CachedValueProvider.Result.create(result, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }
}

abstract class ProtobufBuiltInOptionMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufBuiltInOptionName {
    override fun getReference(): PsiReference {
        return ProtobufBuiltInOptionReference(this)
    }
}

abstract class ProtobufEnumValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufEnumValue {
    override fun getReference(): PsiReference {
        return ProtobufEnumValueReference(this)
    }
}

abstract class ProtobufImportStatementMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufImportStatement {
    override fun getReference(): PsiReference {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(ProtobufImportReference(this), PsiModificationTracker.MODIFICATION_COUNT)
        }
    }
}

abstract class ProtobufFieldNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufFieldName {
    override fun getReference(): PsiReference {
        return ProtobufFieldReference(this)
    }
}

abstract class ProtobufStringValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufStringValue {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }
}
