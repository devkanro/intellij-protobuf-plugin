package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager
import io.kanro.idea.plugin.protobuf.buf.util.BufFiles
import io.kanro.idea.plugin.protobuf.buf.util.isBufConfiguration

class BufFileListener : AsyncFileListener {
    private val logger = Logger.getInstance(BufFileListener::class.java)
    private val filePointer = VirtualFilePointerManager.getInstance().createDirectoryPointer(
        VfsUtil.pathToUrl(BufFiles.getCacheRoot().toString()),
        true,

    )
    private val libraryCacheRoot get() = VirtualFileManager.getInstance().findFileByNioPath(BufFiles.getCacheRoot())

    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier {
        // val validEvents = events.filter { isBufFileEvent(it) || isLibraryDirEvent(it) }
        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                events.forEach {
                    when (it) {
                        is VFileDeleteEvent -> fileDeleted(it)
                        is VFileContentChangeEvent -> fileContentChanged(it)
                        is VFilePropertyChangeEvent -> filePropertyChanged(it)
                        is VFileMoveEvent -> fileMoved(it)
                        is VFileCreateEvent -> fileCreated(it)
                        is VFileCopyEvent -> fileCopied(it)
                    }
                }
            }
        }
    }

    private fun fileDeleted(event: VFileDeleteEvent) {
        logger.info("FILE DELETED: ${event.path}")
    }

    private fun fileContentChanged(event: VFileContentChangeEvent) {
        logger.info("FILE CHANGED: ${event.path}")
    }

    private fun filePropertyChanged(event: VFilePropertyChangeEvent) {
        logger.info("FILE PROPERTY CHANGED: ${event.path}")
    }

    private fun fileMoved(event: VFileMoveEvent) {
        logger.info("FILE MOVED: ${event.path}")
    }

    private fun fileCreated(event: VFileCreateEvent) {
        logger.info("FILE CREATED: ${event.path}")
    }

    private fun fileCopied(event: VFileCopyEvent) {
        logger.info("FILE COPIED: ${event.path}")
    }

    private fun isBufFileEvent(event: VFileEvent): Boolean {
        return when (event) {
            is VFileCreateEvent -> {
                if (event.isDirectory) return false
                isBufConfiguration(event.childName)
            }
            is VFilePropertyChangeEvent -> {
                if (event.file.isDirectory) return false
                if (event.isRename) {
                    return isBufConfiguration(event.oldValue as String) || isBufConfiguration(event.newValue as String)
                }
                isBufConfiguration(event.file.name)
            }
            else -> {
                val file = event.file ?: return false
                if (file.isDirectory) return false
                isBufConfiguration(file.name)
            }
        }
    }

    private fun isLibraryDirEvent(event: VFileEvent): Boolean {
        val root = libraryCacheRoot ?: return false
        return when (event) {
            is VFileCreateEvent -> {
                !VfsUtil.isUnder(event.parent, setOf(root))
            }
            else -> {
                val file = event.file ?: return false
                !VfsUtil.isUnder(file, setOf(root))
            }
        }
    }
}
