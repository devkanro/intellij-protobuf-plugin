package io.kanro.idea.plugin.protobuf.golang

import com.goide.psi.GoNamedElement
import com.goide.psi.GoVarSpec
import com.goide.stubs.index.GoAllPrivateNamesIndex
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex

class GoDecompileService : StartupActivity.RequiredForSmartMode {
    override fun runActivity(project: Project) {
        runBackgroundableTask("Decompiling proto from descriptor", project, true) {
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
