package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager

class BufReimportAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val manager = e.project?.service<BufFileManager>() ?: return
        manager.importProject()
    }
}
