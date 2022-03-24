package io.kanro.idea.plugin.protobuf.buf.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettings

class BufRunConfigurationFactory : ConfigurationFactory(BufRunConfigurationType.INSTANCE) {
    override fun getId(): String {
        return "buf.build"
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        val settings = project.service<BufSettings>()
        val fileManager = project.service<BufFileManager>()
        return BufRunConfiguration(project, this).apply {
            bufPath = settings.bufPath()?.toString()
            command = "build"
            workDir = fileManager.state.modules.firstOrNull()?.path
        }
    }

    override fun getOptionsClass(): Class<out BaseState>? {
        return BufRunConfigurationOptions::class.java
    }
}
