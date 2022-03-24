package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import io.kanro.idea.plugin.protobuf.buf.util.BUF_LOCK
import io.kanro.idea.plugin.protobuf.buf.util.BUF_WORK_YAML
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YAML
import io.kanro.idea.plugin.protobuf.buf.util.isBufConfiguration

class BufFileListener(val project: Project, val fileManager: BufFileManager) : AsyncFileListener {
    private val logger = Logger.getInstance(BufFileListener::class.java)
    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        val validEvents = events.filter { isBufFileEvent(it) }
        if (validEvents.isEmpty()) return null
        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                validEvents.forEach {
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

        if (isProjectEvent(event)) {
            when (event.file.name) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(event.file.parent, null)
                BUF_WORK_YAML -> fileManager.workspaceChanged(event.file.parent, null)
            }
        }

        if (isLibraryEvent(event)) {
            when (event.file.name) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(event.file.parent, null)
            }
        }
    }

    private fun fileContentChanged(event: VFileContentChangeEvent) {
        logger.info("FILE CHANGED: ${event.path}")

        if (isProjectEvent(event)) {
            val path = event.file.parent
            when (event.file.name) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(path, path)
                BUF_WORK_YAML -> fileManager.workspaceChanged(path, path)
            }
        }

        if (isLibraryEvent(event)) {
            val path = event.file.parent
            when (event.file.name) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(path, path)
            }
        }
    }

    private fun filePropertyChanged(event: VFilePropertyChangeEvent) {
        logger.info("FILE PROPERTY CHANGED: ${event.path}")

        if (isProjectEvent(event)) {
            val path = event.file.parent
            when (event.oldValue) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(path, null)
                BUF_WORK_YAML -> fileManager.workspaceChanged(path, null)
            }
            when (event.newValue) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(path, path)
                BUF_WORK_YAML -> fileManager.workspaceChanged(path, path)
            }
        }

        if (isLibraryEvent(event)) {
            val path = event.file.parent
            when (event.oldValue) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(path, null)
            }
            when (event.newValue) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(path, path)
            }
        }
    }

    private fun fileMoved(event: VFileMoveEvent) {
        logger.info("FILE MOVED: ${event.path}")
    }

    private fun fileCreated(event: VFileCreateEvent) {
        logger.info("FILE CREATED: ${event.path}")
        if (isProjectEvent(event)) {
            val path = event.parent
            when (event.childName) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(null, path)
                BUF_WORK_YAML -> fileManager.workspaceChanged(null, path)
            }
        }

        if (isLibraryEvent(event)) {
            val path = event.parent
            when (event.childName) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(null, path)
            }
        }
    }

    private fun fileCopied(event: VFileCopyEvent) {
        logger.info("FILE COPIED: ${event.path}")
        if (isProjectEvent(event)) {
            val path = event.newParent
            when (event.newChildName) {
                BUF_YAML, BUF_LOCK -> fileManager.moduleChanged(null, path)
                BUF_WORK_YAML -> fileManager.workspaceChanged(null, path)
            }
        }

        if (isLibraryEvent(event)) {
            val path = event.newParent
            when (event.newChildName) {
                BUF_YAML, BUF_LOCK -> fileManager.libraryChanged(null, path)
            }
        }
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

    private fun isProjectEvent(event: VFileEvent): Boolean {
        return when (event) {
            is VFileCreateEvent -> {
                ProjectRootManager.getInstance(project).fileIndex.isInContent(event.parent)
            }
            else -> {
                val file = event.file ?: return false
                ProjectRootManager.getInstance(project).fileIndex.isInContent(file)
            }
        }
    }

    private fun isLibraryEvent(event: VFileEvent): Boolean {
        val root = fileManager.cacheRoot() ?: return false
        return when (event) {
            is VFileCreateEvent -> {
                VfsUtil.isUnder(event.parent, setOf(root))
            }
            else -> {
                val file = event.file ?: return false
                VfsUtil.isUnder(file, setOf(root))
            }
        }
    }
}
