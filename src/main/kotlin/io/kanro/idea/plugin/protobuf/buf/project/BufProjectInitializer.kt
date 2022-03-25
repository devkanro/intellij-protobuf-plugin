package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager

class BufProjectInitializer : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        val fileManager = project.service<BufFileManager>()
        VirtualFileManager.getInstance().addAsyncFileListener(BufFileListener(project, fileManager), project)
    }
}
