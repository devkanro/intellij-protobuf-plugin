package io.kanro.idea.plugin.protobuf.buf.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettings
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettingsConfigurable
import io.kanro.idea.plugin.protobuf.buf.util.isBufConfiguration
import java.nio.file.Files
import java.util.function.Function
import javax.swing.JComponent

class BufNotConfiguredNotificationProvider : EditorNotificationProvider, DumbAware {
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?> {
        return Function {
            createNotificationPanel(file, it, project)
        }
    }

    private fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {
        if (!isBufConfiguration(file.name)) return null

        val bufPath = project.service<BufSettings>().bufPath()
            ?: return EditorNotificationPanel().apply {
                text("Buf executable path not configured for sync buf.yaml/buf.work.yaml.")
                icon(AllIcons.General.Warning)
                this.createActionLabel("Setup buf") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, BufSettingsConfigurable::class.java)
                }
            }

        if (!Files.exists(bufPath)) {
            return EditorNotificationPanel().apply {
                text("Wrong buf executable path configured for sync buf.yaml/buf.work.yaml.")
                icon(AllIcons.General.Warning)
                this.createActionLabel("Setup buf") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, BufSettingsConfigurable::class.java)
                }
            }
        }

        return null
    }
}
