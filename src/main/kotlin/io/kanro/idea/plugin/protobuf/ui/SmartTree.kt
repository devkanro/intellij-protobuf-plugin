package io.kanro.idea.plugin.protobuf.ui

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.pom.Navigatable
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.TreeSpeedSearch
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.OpenSourceUtil
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.tree.TreePath

class SmartTree(treeModel: SmartTreeModel) : Tree(treeModel) {
    init {
        isRootVisible = false
        object : DoubleClickListener() {
            override fun onDoubleClick(e: MouseEvent): Boolean {
                handleDoubleClickOrEnter(getClosestPathForLocation(e.x, e.y), e)
                return false
            }
        }.installOn(this)
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER && hasSingleSelection()) {
                    handleDoubleClickOrEnter(selectionPath, e)
                }
            }
        })
        TreeSpeedSearch(this) {
            val wrapper = (it.lastPathComponent as TreeElementWrapper)
            (wrapper.element as? ItemPresentation)?.presentableText
        }
    }

    private fun hasSingleSelection(): Boolean {
        return !isSelectionEmpty && selectionPaths.size == 1
    }

    private fun handleDoubleClickOrEnter(treePath: TreePath, e: InputEvent) {
        ApplicationManager.getApplication().invokeLater({
            val wrapper = treePath.lastPathComponent as? TreeElementWrapper ?: return@invokeLater
            if (!wrapper.isLeaf) return@invokeLater
            val element = wrapper.element
            if (element is TreeActionElement) {
                element.doubleClickOrEnter()
            }
            if (element is Navigatable) {
                OpenSourceUtil.navigate(element)
            }
        }, ModalityState.stateForComponent(this))
        }
    }
    