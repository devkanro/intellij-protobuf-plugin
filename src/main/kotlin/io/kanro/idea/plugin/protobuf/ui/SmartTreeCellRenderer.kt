package io.kanro.idea.plugin.protobuf.ui

import com.intellij.navigation.ColoredItemPresentation
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import javax.swing.JTree

class SmartTreeCellRenderer : ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        if (value !is TreeElementWrapper) return
        val element = value.element
        if (element is ItemPresentation) {
            icon = element.getIcon(false)
            element.presentableText?.let {
                if (element is ColoredItemPresentation) {
                    val textAttributes =
                        EditorColorsManager.getInstance().globalScheme.getAttributes(element.textAttributesKey)
                    append(it, SimpleTextAttributes.fromTextAttributes(textAttributes))
                } else {
                    append(it)
                }
            }
        }

        if (element is TooltipPresentation) {
            toolTipText = element.tooltip()
        }
    }
}
