package io.kanro.idea.plugin.protobuf.golang

import com.goide.psi.GoNamedElement
import com.goide.psi.GoVarSpec
import com.goide.stubs.index.GoAllPrivateNamesIndex
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class GoDecompileService : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (!project.getService(ProtobufSettings::class.java).state.autoDecompile) return

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
