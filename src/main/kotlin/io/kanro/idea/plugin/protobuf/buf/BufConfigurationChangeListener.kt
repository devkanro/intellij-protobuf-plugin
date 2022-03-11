package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import io.kanro.idea.plugin.protobuf.buf.util.isBufLock
import io.kanro.idea.plugin.protobuf.buf.util.isBufWorkYaml
import io.kanro.idea.plugin.protobuf.buf.util.isBufYaml

class BufConfigurationChangeListener : AsyncFileListener {
    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        events.forEach {
            val fileName = it.file?.name ?: return@forEach
            if (isBufYaml(fileName) || isBufWorkYaml(fileName) || isBufLock(fileName)) {
                BufConfigurationModificationTracker.incModificationCount()
            }
        }
        return null
    }
}
