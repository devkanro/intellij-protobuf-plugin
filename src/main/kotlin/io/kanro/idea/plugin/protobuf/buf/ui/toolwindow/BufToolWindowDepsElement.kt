package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon

class BufToolWindowDepsElement(
    val manager: BufFileManager,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        val locked = module.lockedDependencies.associateBy { it.nameWithoutCommit() }
        val resolved = mutableSetOf<String>()
        return module.dependencies.map {
            val dep = locked[it]
            val lib = manager.findLibrary(dep)
            lib?.reference?.let {
                resolved += it
            }
            BufToolWindowDepElement(manager, module, it, dep, lib, resolved)
        }.toTypedArray()
    }

    override fun getPresentableText(): String? {
        return "Dependencies"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.PpLibFolder
    }
}
