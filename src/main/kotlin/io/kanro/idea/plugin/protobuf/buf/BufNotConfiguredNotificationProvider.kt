package io.kanro.idea.plugin.protobuf.buf

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettings
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettingsConfigurable
import java.nio.file.Files
import kotlin.io.path.Path

class BufNotConfiguredNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {
        when (file.name.lowercase()) {
            "buf.yaml", "buf.yml", "buf.work.yaml", "buf.work.yml", "buf.gen.yaml", "buf.gen.yaml", "buf.lock" -> {}
            else -> return null
        }

        val bufSettings = project.getService(BufSettings::class.java) ?: return null
        if (bufSettings.state.path.isEmpty()) {
            return EditorNotificationPanel().apply {
                text("Buf executable path not configured for sync buf.yaml/buf.work.yaml.")
                icon(AllIcons.General.Warning)
                this.createActionLabel("Setup buf") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, BufSettingsConfigurable::class.java)
                }
            }
        }

        if (!Files.exists(Path(bufSettings.state.path))) {
            return EditorNotificationPanel().apply {
                text("Wrong buf executable path configured for sync buf.yaml/buf.work.yaml.")
                icon(AllIcons.General.Warning)
                this.createActionLabel("Setup buf") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, BufSettingsConfigurable::class.java)
                }
            }
        }

        return super.createNotificationPanel(file, fileEditor, project)
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("BufNotConfiguredNotification")
    }
}
