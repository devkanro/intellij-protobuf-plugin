package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.layout.panel
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import com.intellij.util.ui.components.BorderLayoutPanel
import io.kanro.idea.plugin.protobuf.lang.util.contentEquals
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class ProtobufSettingsComponent(val project: Project) : ConfigurableUi<ProtobufSettings> {
    private val panel: JPanel
    private val importRootsModel = ListTableModel<ProtobufSettings.ImportRootEntry>(PathColumnInfo)

    init {
        val tablePanel = BorderLayoutPanel()
        val tableView = TableView(importRootsModel)
        val decorator = ToolbarDecorator.createDecorator(tableView, null)
        decorator.setAddAction {
            val selectedFile =
                FileChooser.chooseFile(protobufRootChooserDescriptor(), project, null) ?: return@setAddAction
            importRootsModel.addRow(ProtobufSettings.ImportRootEntry(selectedFile.url))
        }
        decorator.setEditAction {
            tableView.editCellAt(tableView.selectedRow, tableView.selectedColumn)
        }
        tablePanel.add(decorator.createPanel(), BorderLayout.CENTER)
        panel = tablePanel
    }

    override fun reset(settings: ProtobufSettings) {
        importRootsModel.items = settings.state.importRoots.toMutableList()
    }

    override fun isModified(settings: ProtobufSettings): Boolean {
        return !importRootsModel.items.contentEquals(settings.state.importRoots)
    }

    override fun apply(settings: ProtobufSettings) {
        settings.state.importRoots = importRootsModel.items.toList()
        ApplicationManager.getApplication().runWriteAction {
            ProjectRootManagerEx.getInstanceEx(project).makeRootsChange({}, false, true)
        }
    }

    override fun getComponent(): JComponent {
        return panel
    }

    private fun protobufRootChooserDescriptor(): FileChooserDescriptor {
        return FileChooserDescriptor(
            false,
            true,
            true,
            true,
            true,
            false
        ).withShowFileSystemRoots(true).withTitle("Choose Protobuf Path Root")
    }

    object PathColumnInfo : ColumnInfo<ProtobufSettings.ImportRootEntry, String>("Path") {
        override fun valueOf(item: ProtobufSettings.ImportRootEntry?): String? {
            return item?.path
        }

        override fun setValue(item: ProtobufSettings.ImportRootEntry?, value: String?) {
            value?.let { item?.path = it }
        }

        override fun isCellEditable(item: ProtobufSettings.ImportRootEntry?): Boolean {
            return true
        }
    }
}
