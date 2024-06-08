package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufOptionNameReference

abstract class ProtobufOptionNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufOptionName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return if (symbolName != null) {
            arrayOf(ProtobufOptionNameReference(this))
        } else {
            emptyArray()
        }
    }

    override fun symbol(): QualifiedName? {
        return null
    }

    override fun rename(qualifiedName: QualifiedName) {
    }

    override fun leaf(): ProtobufOptionName {
        var result: ProtobufOptionName = this
        while (true) {
            result = result.optionName ?: break
        }
        return result
    }

    override fun root(): ProtobufOptionName {
        var result: ProtobufOptionName = this
        while (true) {
            result = result.parent as? ProtobufOptionName ?: break
        }
        return result
    }
}
