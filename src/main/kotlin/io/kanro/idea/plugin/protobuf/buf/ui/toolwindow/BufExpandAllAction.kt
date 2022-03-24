package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import com.intellij.ide.DefaultTreeExpander
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import io.kanro.idea.plugin.protobuf.ui.SmartTree

class BufExpandAllAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val tree = e.getData(PlatformDataKeys.CONTEXT_COMPONENT) as SmartTree
        ApplicationManager.getApplication().invokeLater {
            DefaultTreeExpander(tree).expandAll()
        }
    }
}

class BufCollapseAllAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val tree = e.getData(PlatformDataKeys.CONTEXT_COMPONENT) as SmartTree
        ApplicationManager.getApplication().invokeLater {
            DefaultTreeExpander(tree).collapseAll()
        }
    }
}
