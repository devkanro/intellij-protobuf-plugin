package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElementBase
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
