package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon

class BufToolWindowTasksElement(
    val manager: BufFileManager,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf(
            BufToolWindowTaskElement(
                manager, module, "Sync",
                "Run 'buf mod update' in ${module.name ?: module.path}.", "mod", "update"
            ),
            BufToolWindowTaskElement(
                manager, module, "Build",
                "Run 'buf build' in ${module.name ?: module.path}.", "build"
            ),
            BufToolWindowTaskElement(
                manager, module, "Lint",
                "Run 'buf lint' in ${module.name ?: module.path}.", "lint"
            ),
            BufToolWindowTaskElement(
                manager, module, "Generate",
                "Run 'buf generate' in ${module.name ?: module.path}.", "generate"
            ),
            BufToolWindowTaskElement(
                manager, module, "Push",
                "Run 'buf push' in ${module.name ?: module.path}.", "push"
            ),
        )
    }

    override fun getPresentableText(): String? {
        return "Tasks"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.ConfigFolder
    }
}
