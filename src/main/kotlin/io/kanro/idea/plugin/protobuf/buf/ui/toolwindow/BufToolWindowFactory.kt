package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager

class BufToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun shouldBeAvailable(project: Project): Boolean {
        val manager = project.service<BufFileManager>()
        return manager.state.modules.isNotEmpty()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val manager = project.service<BufFileManager>()
        manager.createToolWindowContent(toolWindow)
    }

    override fun init(toolWindow: ToolWindow) {
        toolWindow.stripeTitle = "Buf"
    }
}
