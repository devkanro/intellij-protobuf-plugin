package io.kanro.idea.plugin.protobuf.buf.ui.toolwindow

import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.ui.TreeElement

class BufToolWindowRootElement(val manager: BufFileManager) : TreeElement {
    override fun children(): Array<TreeElement> {
        return manager.state.modules.map { BufToolWindowModuleElement(manager, it) }.toTypedArray()
    }
}
