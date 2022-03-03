package io.kanro.idea.plugin.protobuf.lang.actions

import com.intellij.openapi.actionSystem.DefaultActionGroup

class ProtobufActionGroup : DefaultActionGroup() {
    override fun hideIfNoVisibleChildren(): Boolean {
        return true
    }
}
