package io.kanro.idea.plugin.protobuf.lang.psi.proto.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue

abstract class ProtobufStringValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufStringValue {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }

    override fun symbol(): QualifiedName? {
        return value().takeIf { it.isNotEmpty() }?.let { QualifiedName.fromDottedString(it) }
    }

    override fun rename(qualifiedName: QualifiedName) {
        replace(ProtobufPsiFactory.createStringValue(project, qualifiedName.toString()))
    }
}
