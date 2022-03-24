package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.icons.AllIcons
import com.intellij.navigation.ColoredItemPresentation
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.buf.run.BufRunConfiguration
import io.kanro.idea.plugin.protobuf.buf.run.BufRunConfigurationType
import io.kanro.idea.plugin.protobuf.ui.TooltipPresentation
import io.kanro.idea.plugin.protobuf.ui.TreeActionElement
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon
import kotlin.io.path.Path
import kotlin.io.path.name

class BufToolWindowRootElement(val manager: BufFileManager) : TreeElement {
    override fun children(): Array<TreeElement> {
        return manager.state.modules.map { BufToolWindowModuleElement(manager, it) }.toTypedArray()
    }
}

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

class BufToolWindowDepElement(
    val manager: BufFileManager,
    val module: BufFileManager.State.Module,
    val dependencyName: String,
    val dependency: BufFileManager.State.Dependency?,
    val library: BufFileManager.State.Module?,
    val resolvedDeps: MutableSet<String> = mutableSetOf()
) : TreeElement, ColoredItemPresentation, TooltipPresentation {
    override fun children(): Array<TreeElement> {
        library ?: return arrayOf()
        val locked = library.lockedDependencies.associateBy { it.nameWithoutCommit() }
        return library.dependencies.mapNotNull {
            val dep = locked[it]
            val lib = manager.findLibrary(dep)
            if (lib?.reference in resolvedDeps) {
                return@mapNotNull null
            }
            lib?.reference?.let {
                resolvedDeps += it
            }
            BufToolWindowDepElement(manager, library, it, dep, lib, resolvedDeps)
        }.toTypedArray()
    }

    override fun getPresentableText(): String {
        return library?.reference ?: dependency?.name() ?: dependencyName
    }

    override fun getIcon(unused: Boolean): Icon {
        return AllIcons.Nodes.PpLib
    }

    override fun getTextAttributesKey(): TextAttributesKey? {
        library ?: return HighlightInfoType.ERROR.attributesKey
        return null
    }

    override fun tooltip(): String? {
        dependency ?: return "Unsolved dependency for '$dependencyName', check your dependency name or run 'buf build'"
        library ?: return "Un-synced dependency for '$dependencyName', run 'buf build' to sync dependency"
        return null
    }
}
