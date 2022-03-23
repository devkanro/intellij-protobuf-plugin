package io.kanro.idea.plugin.protobuf.ui

import javax.swing.tree.DefaultTreeModel

class SmartTreeModel(root: TreeElement) : DefaultTreeModel(TreeElementWrapper(root, null))
