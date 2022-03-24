package io.kanro.idea.plugin.protobuf.ui

interface TreeElement {
    fun children(): Array<TreeElement>
}

interface TreeActionElement : TreeElement {
    fun doubleClickOrEnter()
}
