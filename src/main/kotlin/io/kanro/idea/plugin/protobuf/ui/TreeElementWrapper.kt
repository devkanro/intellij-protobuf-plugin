package io.kanro.idea.plugin.protobuf.ui

import java.util.Collections
import java.util.Enumeration
import javax.swing.tree.TreeNode

class TreeElementWrapper(val element: TreeElement, private val parent: TreeElementWrapper?) : TreeNode {
    private var children = element.children().map { TreeElementWrapper(it, this) }

    fun reload() {
        children = element.children().map { TreeElementWrapper(it, this) }
    }

    override fun getChildAt(childIndex: Int): TreeNode {
        return children[childIndex]
    }

    override fun getChildCount(): Int {
        return children.size
    }

    override fun getIndex(node: TreeNode?): Int {
        return children.indexOf(node)
    }

    override fun children(): Enumeration<out TreeNode> {
        return Collections.enumeration(children)
    }

    override fun getParent(): TreeNode? {
        return parent
    }

    override fun getAllowsChildren(): Boolean {
        return children.isNotEmpty()
    }

    override fun isLeaf(): Boolean {
        return children.isEmpty()
    }
}
