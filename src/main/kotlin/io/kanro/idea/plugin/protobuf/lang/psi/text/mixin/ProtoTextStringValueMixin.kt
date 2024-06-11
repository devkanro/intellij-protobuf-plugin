package io.kanro.idea.plugin.protobuf.lang.psi.text.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextStringValue

abstract class ProtoTextStringValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtoTextStringValue {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this)
    }

    override fun symbol(): QualifiedName? {
        return (reference?.resolve() as? ProtobufScope)?.scope()
    }

    override fun rename(qualifiedName: QualifiedName) {
        replace(ProtoTextPsiFactory.createStringValue(project, qualifiedName.toString()))
    }
}
