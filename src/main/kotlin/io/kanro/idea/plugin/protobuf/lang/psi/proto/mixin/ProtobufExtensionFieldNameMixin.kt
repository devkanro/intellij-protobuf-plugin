package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.reference.ProtobufExtensionFieldReference

abstract class ProtobufExtensionFieldNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufExtensionFieldName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return arrayOf(ProtobufExtensionFieldReference(this))
    }

    override fun symbol(): QualifiedName? {
        return buildList<String> {
            var parent = this@ProtobufExtensionFieldNameMixin as? ProtobufExtensionFieldName
            while (parent != null) {
                add(parent.symbolName.text)
                parent = parent.parent as? ProtobufExtensionFieldName
            }
        }.reversed().let { QualifiedName.fromComponents(it) }
    }

    override fun rename(qualifiedName: QualifiedName) {
        replace(ProtobufPsiFactory.createExtensionFieldName(project, qualifiedName.toString()))
    }

    override fun leaf(): ProtobufExtensionFieldName {
        var result: ProtobufExtensionFieldName = this
        while (true) {
            result = result.extensionFieldName ?: break
        }
        return result
    }

    override fun root(): ProtobufExtensionFieldName {
        var result: ProtobufExtensionFieldName = this
        while (true) {
            result = result.parent as? ProtobufExtensionFieldName ?: break
        }
        return result
    }
}
