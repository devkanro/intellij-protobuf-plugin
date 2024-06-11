package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufTypeNameReference

abstract class ProtobufTypeNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufTypeName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return arrayOf(ProtobufTypeNameReference(this))
    }

    override fun symbol(): QualifiedName? {
        return buildList<String> {
            var parent = this@ProtobufTypeNameMixin as? ProtobufTypeName
            while (parent != null) {
                add(parent.symbolName.text)
                parent = parent.parent as? ProtobufTypeName
            }
        }.reversed().let { QualifiedName.fromComponents(it) }
    }

    override fun rename(qualifiedName: QualifiedName) {
        replace(ProtobufPsiFactory.createTypeName(project, qualifiedName.toString()))
    }

    override fun leaf(): ProtobufTypeName {
        var result: ProtobufTypeName = this
        while (true) {
            result = result.typeName ?: break
        }
        return result
    }

    override fun root(): ProtobufTypeName {
        var result: ProtobufTypeName = this
        while (true) {
            result = result.parent as? ProtobufTypeName ?: break
        }
        return result
    }
}
