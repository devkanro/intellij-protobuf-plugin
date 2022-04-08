package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import com.intellij.util.ui.components.BorderLayoutPanel
import io.kanro.idea.plugin.protobuf.lang.util.contentEquals
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class ProtobufSettingsComponent(val project: Project) : ConfigurableUi<ProtobufSettings> {
    private val panel: JPanel
    private val importRootsModel = ListTableModel<ProtobufSettings.ImportRootEntry>(PathColumnInfo, CommonColumnInfo)

    init {
        val tablePanel = BorderLayoutPanel()
        val tableView = TableView(importRootsModel)
        val decorator = ToolbarDecorator.createDecorator(tableView, null)
        decorator.setAddAction {
            val selectedFile =
                FileChooser.chooseFile(protobufRootChooserDescriptor(), project, null) ?: return@setAddAction
            val file = VirtualFileManager.getInstance().findFileByUrl(selectedFile.url) ?: return@setAddAction
            val contentRoot = ProjectRootManagerEx.getInstanceEx(project).fileIndex.getContentRootForFile(file)
            val sourceRoot = ProjectRootManagerEx.getInstanceEx(project).fileIndex.getSourceRootForFile(file)
            importRootsModel.addRow(
                ProtobufSettings.ImportRootEntry(
                    selectedFile.url,
                    contentRoot == null && sourceRoot == null
                )
            )
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
        return !settings.state.importRoots.contentEquals(importRootsModel.items)
    }

    override fun apply(settings: ProtobufSettings) {
        settings.state.importRoots = importRootsModel.items.toMutableList()
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

    object CommonColumnInfo : ColumnInfo<ProtobufSettings.ImportRootEntry, Boolean>("Common") {
        override fun valueOf(item: ProtobufSettings.ImportRootEntry?): Boolean? {
            return item?.common
        }

        override fun setValue(item: ProtobufSettings.ImportRootEntry?, value: Boolean?) {
            value?.let { item?.common = it }
        }

        override fun isCellEditable(item: ProtobufSettings.ImportRootEntry?): Boolean {
            return true
        }

        override fun getEditor(item: ProtobufSettings.ImportRootEntry?): TableCellEditor? {
            return BooleanTableCellEditor()
        }

        override fun getWidth(table: JTable?): Int {
            return 64
        }

        override fun getCustomizedRenderer(
            o: ProtobufSettings.ImportRootEntry?,
            renderer: TableCellRenderer?
        ): TableCellRenderer {
            return BooleanTableCellRenderer()
        }

        override fun getColumnClass(): Class<*> {
            return Boolean::class.java
        }
    }
}
