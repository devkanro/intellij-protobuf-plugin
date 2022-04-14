package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

abstract class CachedProtobufRootProvider : ProtobufRootProvider {
    override fun roots(context: PsiElement): List<ProtobufRoot> {
        val file = context.containingFile
        return CachedValuesManager.getManager(context.project).getCachedValue(file) {
            val roots = collectRoots(file)
            CachedValueProvider.Result.create(roots, modificationTracker(file))
        }
    }

    protected abstract fun collectRoots(context: PsiElement): List<ProtobufRoot>

    abstract fun modificationTracker(context: PsiElement): ModificationTracker
}
