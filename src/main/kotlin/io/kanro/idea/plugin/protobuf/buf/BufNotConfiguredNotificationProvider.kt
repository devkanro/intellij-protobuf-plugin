package io.kanro.idea.plugin.protobuf.buf

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettings
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettingsConfigurable
import java.nio.file.Files
import kotlin.io.path.Path

class BufNotConfiguredNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>(), DumbAware {
    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {
        when (file.name.lowercase()) {
            "buf.yaml", "buf.yml", "buf.work.yaml", "buf.work.yml", "buf.gen.yaml", "buf.gen.yml", "buf.lock" -> {}
            else -> return null
        }

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

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("BufNotConfiguredNotification")
    }
}
