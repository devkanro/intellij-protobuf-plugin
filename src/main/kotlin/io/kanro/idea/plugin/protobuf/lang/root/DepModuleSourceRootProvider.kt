package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.util.module

class DepModuleSourceRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "moduleDeps"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        return context.module?.let {
            ModuleRootManager.getInstance(it).orderEntries()
                .withoutLibraries()
                .withoutSdk().allSourceRoots.map {
                    ProtobufRoot(null, it)
                }
        } ?: listOf()
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return ProjectRootModificationTracker.getInstance(context.project)
    }
}
