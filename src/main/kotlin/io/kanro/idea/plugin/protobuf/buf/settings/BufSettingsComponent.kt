package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorNotifications
import com.intellij.ui.dsl.builder.panel
import java.io.File
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class BufSettingsComponent(val project: Project) : ConfigurableUi<BufSettings> {
    private val panel: JPanel
    private lateinit var textField: JTextField

    init {
        panel = panel {
            row {
                label("Buf executable:")
                textFieldWithBrowseButton("Choose Buf Executable Path", project, bufPathChooserDescriptor()) {
                    textField.text = it.path
                    File(it.path).path
                }.apply {
                    textField = this.component.textField
                }
            }
        }
    }

    override fun reset(settings: BufSettings) {
        textField.text = settings.state.path
    }

    override fun isModified(settings: BufSettings): Boolean {
        return textField.text != settings.state.path
    }

    override fun apply(settings: BufSettings) {
        settings.state.path = textField.text
        EditorNotifications.getInstance(project).updateAllNotifications()
    }

    override fun getComponent(): JComponent {
        return panel
    }

    private fun bufPathChooserDescriptor(): FileChooserDescriptor {
        return FileChooserDescriptor(
            true,
            false,
            false,
            false,
            false,
            false
        ).withShowFileSystemRoots(true)
    }
}
