package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import io.kanro.idea.plugin.protobuf.buf.BufConfigurationModificationTracker
import io.kanro.idea.plugin.protobuf.buf.util.BufFiles
import io.kanro.idea.plugin.protobuf.buf.util.isBufConfiguration
import io.kanro.idea.plugin.protobuf.buf.util.isBufLock
import io.kanro.idea.plugin.protobuf.buf.util.isBufWorkYaml
import io.kanro.idea.plugin.protobuf.buf.util.isBufYaml

class BufFileListener : AsyncFileListener {
    private val libraryCacheRoot get() = VirtualFileManager.getInstance().findFileByNioPath(BufFiles.getCacheRoot())

    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        events.forEach {
            val fileName = it.file?.name ?: return@forEach
            if (isBufYaml(fileName) || isBufWorkYaml(fileName) || isBufLock(fileName)) {
                BufConfigurationModificationTracker.incModificationCount()
            }
        }
        return null
    }

    private fun isBufFileEvent(event: VFileEvent): Boolean {
        when (event) {
            is VFileCreateEvent -> {
                if(event.isDirectory) return false
                return isBufConfiguration(event.childName)
            }
            is VFilePropertyChangeEvent -> {
                if(event.file.isDirectory) return false
                if (event.isRename) {
                    return isBufConfiguration(event.oldValue as String) || isBufConfiguration(event.newValue as String)
                }
                return isBufConfiguration(event.file.name)
            }
            else -> {
                val file = event.file ?: return false
                if (file.isDirectory) return false
                if (!VfsUtil.isUnder(file, setOf(root))) return false
            }
        }
    }

    private fun isLibraryDirEvent(event: VFileEvent): Boolean {
        val root = libraryCacheRoot ?: return false
        when (event) {
            is VFileCreateEvent -> {
                if (!event.isDirectory) return false
                if (!VfsUtil.isUnder(event.parent, setOf(root))) return false
            }
            else -> {
                val file = event.file ?: return false
                if (!file.isDirectory) return false
                if (!VfsUtil.isUnder(file, setOf(root))) return false
            }
        }

        return true
    }
}
