package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.lang.util.module

class LibraryRootProvider : ProtobufRootProvider {
    override fun id(): String? {
        return "library"
    }

    override fun roots(context: PsiElement): List<ProtobufRoot> {
        return context.module?.let {
            ModuleRootManager.getInstance(it).orderEntries().allLibrariesAndSdkClassesRoots.map {
                ProtobufRoot(null, it)
            }
        } ?: ProjectRootManager.getInstance(context.project).orderEntries().allLibrariesAndSdkClassesRoots.map {
            ProtobufRoot(null, it)
        }
    }

    override fun searchScope(context: PsiElement): GlobalSearchScope? {
        return context.module?.moduleWithLibrariesScope
    }
}
