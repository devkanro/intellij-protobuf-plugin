package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.buf.run.BufRunConfiguration
import io.kanro.idea.plugin.protobuf.buf.run.BufRunConfigurationType
import io.kanro.idea.plugin.protobuf.ui.TooltipPresentation
import io.kanro.idea.plugin.protobuf.ui.TreeActionElement
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon
import kotlin.io.path.Path
import kotlin.io.path.name

class BufToolWindowTaskElement(
    val manager: BufFileManager,
    val module: BufFileManager.State.Module,
    val name: String,
    val tooltip: String?,
    val command: String,
    val parameters: String = ""
) : TreeActionElement, ItemPresentation, TooltipPresentation {
    override fun children(): Array<TreeElement> {
        return arrayOf()
    }

    override fun getPresentableText(): String? {
        return name
    }

    override fun getIcon(unused: Boolean): Icon? {
        return AllIcons.Nodes.Editorconfig
    }

    override fun tooltip(): String? {
        return tooltip
    }

    override fun doubleClickOrEnter() {
        val moduleName = module.name ?: module.path?.let { Path(it).name }
        val runManager = RunManager.getInstance(manager.project)
        val configuration = runManager.getConfigurationSettingsList(BufRunConfigurationType.INSTANCE).firstOrNull {
            val buf = it.configuration as BufRunConfiguration
            buf.command == command && buf.workDir == module.path
        } ?: runManager.createConfiguration("$moduleName [buf $command]", BufRunConfigurationType::class.java).apply {
            val buf = configuration as BufRunConfiguration
            buf.command = command
            buf.workDir = module.path
            buf.parameters = parameters
        }
        ProgramRunnerUtil.executeConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance())
    }
}
