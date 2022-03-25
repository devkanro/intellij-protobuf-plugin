package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.icons.AllIcons
import com.intellij.navigation.ColoredItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TooltipPresentation
import io.kanro.idea.plugin.protobuf.ui.TreeElement
import javax.swing.Icon

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
