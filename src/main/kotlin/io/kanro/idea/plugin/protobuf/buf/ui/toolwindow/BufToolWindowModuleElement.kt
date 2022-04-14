package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon
import kotlin.io.path.Path
import kotlin.io.path.name

class BufToolWindowModuleElement(
    val manager: BufFileManager,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf(
            BufToolWindowTasksElement(manager, module),
            BufToolWindowDepsElement(manager, module)
        )
    }

    override fun getPresentableText(): String? {
        return module.name ?: module.path?.let { Path(it).name }
    }

    override fun getIcon(unused: Boolean): Icon {
        return AllIcons.Nodes.Module
    }
}
