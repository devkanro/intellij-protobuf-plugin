package io.kanro.idea.plugin.protobuf.buf.run

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.execution.ParametersListUtil
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import javax.swing.JComponent
import javax.swing.JList

class BufRunConfigurationEditor(private val project: Project) : SettingsEditor<BufRunConfiguration>() {
    var bufPath: String = ""
    var command: String = ""
    var target: Any? = null
    var parameters: String = ""
    private lateinit var dialogPanel: DialogPanel

    override fun resetEditorFrom(s: BufRunConfiguration) {
        val manager = project.service<BufFileManager>()
        bufPath = s.bufPath ?: ""
        command = s.command ?: ""
        target = manager.state.modules.firstOrNull { it.path == s.workDir }
            ?: manager.state.workspaces.firstOrNull { it.path == s.workDir }
        parameters = s.parameters ?: ""
        dialogPanel.reset()
    }

    override fun applyEditorTo(s: BufRunConfiguration) {
        s.bufPath = bufPath
        s.command = command
        s.workDir = when (val t = target) {
            is BufFileManager.State.Module -> t.path!!
            is BufFileManager.State.Workspace -> t.path!!
            else -> ""
        }
        s.parameters = parameters
    }

    override fun createEditor(): JComponent {
        dialogPanel = panel {
            row {
                label("Buf Executable:")
                textFieldWithBrowseButton("Buf") {
                    it.path
                }.bindText(::bufPath)
            }
            row {
                label("Command:")
                comboBox(arrayOf("build", "lint", "generate", "break", "push", "mod")).bindItem(::command).applyToComponent {
                    this.isEditable = true
                }
            }
            row {
                label("Target:")
                val manager = project.service<BufFileManager>()
                val t = comboBox(
                    (manager.state.workspaces + manager.state.modules).toTypedArray(),
                    BufWorkspaceAndModuleListCellRenderer
                ).bindItem({
                    target
                }, {
                    target = it
                })
            }
            row {
                label("Arguments:")
                cell(
                    ExpandableTextField(
                        ParametersListUtil.DEFAULT_LINE_PARSER,
                        ParametersListUtil.DEFAULT_LINE_JOINER
                    )
                ).bindText(this@BufRunConfigurationEditor::parameters)
            }
        }
        return dialogPanel
    }

    object BufWorkspaceAndModuleListCellRenderer : ColoredListCellRenderer<Any?>() {
        override fun customizeCellRenderer(
            list: JList<out Any>,
            value: Any?,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) {
            when (value) {
                is BufFileManager.State.Module -> {
                    icon = AllIcons.Nodes.Module
                    append("Module: ${value.name ?: value.path}")
                }
                is BufFileManager.State.Workspace -> {
                    icon = AllIcons.Nodes.ModuleGroup
                    append("Workspace: ${value.path}")
                }
            }
        }
    }
}
