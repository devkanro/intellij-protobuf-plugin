package io.kanro.idea.plugin.protobuf.golang

import com.goide.project.GoRootsProvider
import com.goide.psi.GoNamedElement
import com.goide.psi.GoVarSpec
import com.goide.stubs.index.GoAllPrivateNamesIndex
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.file.RootsFileResolver

class GoRootResolver : RootsFileResolver() {
    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return GoRootsProvider.EP_NAME.extensionList.flatMap { it.getGoPathSourcesRoots(project, null) }
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return GoRootsProvider.EP_NAME.extensionList.flatMap { it.getGoPathSourcesRoots(module.project, module) }
    }
}

class GoDecompileService : StartupActivity.RequiredForSmartMode {
    override fun runActivity(project: Project) {
        runBackgroundableTask("Decompiling Proto From Descriptor", project, true) {
            it.isIndeterminate = true
            val pattern = PlatformPatterns.string().endsWith("_proto_rawDesc")
            ApplicationManager.getApplication().runReadAction {
                val keys = StubIndex.getInstance().getAllKeys(GoAllPrivateNamesIndex.ALL_PRIVATE_NAMES, project)
                it.isIndeterminate = false
                keys.forEachIndexed { index, s ->
                    it.fraction = index.toDouble() / keys.count()
                    if (!pattern.accepts(s)) return@forEachIndexed
                    val elements = StubIndex.getElements(
                        GoAllPrivateNamesIndex.ALL_PRIVATE_NAMES, s,
                        project,
                        GlobalSearchScope.allScope(project),
                        GoNamedElement::class.java
                    )
                    elements.firstOrNull()?.let {
                        (it.parent as? GoVarSpec)?.decompile()
                    }
                }
            }
        }
    }
}
