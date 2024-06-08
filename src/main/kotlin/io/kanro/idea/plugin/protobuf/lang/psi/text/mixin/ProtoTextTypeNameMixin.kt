package io.kanro.idea.plugin.protobuf.lang.psi.text.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextTypeNameReference

abstract class ProtoTextTypeNameMixin(node: ASTNode) : ProtoTextElementBase(node), ProtoTextTypeName {
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result(
                arrayOf(ProtoTextTypeNameReference(this)),
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
    }

    override fun symbol(): QualifiedName? {
        return buildList<String> {
            var parent = parent as? ProtoTextTypeName
            while (parent != null) {
                add(parent.symbolName.text)
                parent = parent.parent as? ProtoTextTypeName
            }
        }.reversed().let { QualifiedName.fromComponents(it) }
    }

    override fun rename(qualifiedName: QualifiedName) {
        replace(ProtoTextPsiFactory.createTypeName(project, qualifiedName.toString()))
    }

    override fun leaf(): ProtoTextTypeName {
        var result: ProtoTextTypeName = this
        while (true) {
            result = result.typeName ?: break
        }
        return result
    }

    override fun root(): ProtoTextTypeName {
        var result: ProtoTextTypeName = this
        while (true) {
            result = result.parent as? ProtoTextTypeName ?: break
        }
        return result
    }
}
