package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager

class BufProjectInitializer : ProjectActivity {
    override suspend fun execute(project: Project) {
        val fileManager = project.service<BufFileManager>()
        VirtualFileManager.getInstance().addAsyncFileListener(BufFileListener(project, fileManager), project)
    }
}
