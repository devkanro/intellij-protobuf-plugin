package io.kanro.idea.plugin.protobuf.buf.ui

import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon
import kotlin.io.path.Path
import kotlin.io.path.name

class BufToolWindowRootElement(val project: Project) : TreeElement {
    override fun children(): Array<TreeElement> {
        val state = project.service<BufFileManager>().state
        return state.modules.map { BufToolWindowModuleElement(state, it) }.toTypedArray()
    }
}

class BufToolWindowModuleElement(
    val state: BufFileManager.State,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf(BufToolWindowTasksElement(state, module))
    }

    override fun getPresentableText(): String? {
        return module.name ?: module.path?.let { Path(it).name }
    }

    override fun getIcon(unused: Boolean): Icon {
        return AllIcons.Nodes.Module
    }
}

class BufToolWindowTasksElement(
    val state: BufFileManager.State,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf(
            BufToolWindowTaskElement(state, module, "Build"),
            BufToolWindowTaskElement(state, module, "Lint"),
            BufToolWindowTaskElement(state, module, "Publish"),
        )
    }

    override fun getPresentableText(): String? {
        return "Tasks"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.ConfigFolder
    }
}

class BufToolWindowTaskElement(
    val state: BufFileManager.State,
    val module: BufFileManager.State.Module,
    val name: String
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf()
    }

    override fun getPresentableText(): String? {
        return name
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.Editorconfig
    }
}

class BufToolWindowDepsElement(
    val state: BufFileManager.State,
    val module: BufFileManager.State.Module
) : TreeElement, ItemPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf()
    }

    override fun getPresentableText(): String? {
        return "Dependencies"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.PpLibFolder
    }
}
