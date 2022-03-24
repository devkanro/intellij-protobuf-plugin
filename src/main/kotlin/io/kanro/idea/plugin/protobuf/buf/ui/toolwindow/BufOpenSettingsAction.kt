package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettingsConfigurable

class BufOpenSettingsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ShowSettingsUtil.getInstance().showSettingsDialog(project, BufSettingsConfigurable::class.java)
    }
}
