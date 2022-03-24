package io.kanro.idea.plugin.protobuf.ui

import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode

class SmartTreeModel(root: TreeElement) : DefaultTreeModel(TreeElementWrapper(root, null)) {
    override fun reload(node: TreeNode?) {
        if (node !is TreeElementWrapper) return
        node.reload()
        super.reload(node)
    }
}
