package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.panel
import java.io.File
import javax.swing.JComponent
import javax.swing.JPanel

class BufSettingsComponent(val project: Project) : ConfigurableUi<BufSettings> {
    private val panel: JPanel
    private var path = ""

    init {
        panel = panel {
            row {
                label("Buf execuable:")
                textFieldWithBrowseButton("Choose Buf Execuable Path", project, bufPathChooserDescriptor()) {
                    path = it.path
                    File(path).path
                }
            }
        }
    }

    override fun reset(settings: BufSettings) {
        path = settings.state.path
    }

    override fun isModified(settings: BufSettings): Boolean {
        return path != settings.state.path
    }

    override fun apply(settings: BufSettings) {
        settings.state.path = path
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
