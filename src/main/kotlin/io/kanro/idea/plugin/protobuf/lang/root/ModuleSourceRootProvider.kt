package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.lang.util.module

class ModuleSourceRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "moduleSource"
    }

    override fun roots(context: PsiElement): List<ProtobufRoot> {
        return context.module?.let {
            ModuleRootManager.getInstance(it).sourceRoots.map {
                ProtobufRoot(null, it)
            }
        } ?: listOf()
    }

    override fun searchScope(context: PsiElement): GlobalSearchScope? {
        return context.module?.let {
            it.moduleScope.union(it.moduleContentScope)
        }
    }
}
