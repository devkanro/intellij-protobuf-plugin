package io.kanro.idea.plugin.protobuf.buf.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import java.io.File

class BufRunConfiguration(
    project: Project,
    factory: ConfigurationFactory
) : LocatableConfigurationBase<BufRunConfigurationOptions>(project, factory) {
    var bufPath: String?
        get() = options.bufPath
        set(value) {
            options.bufPath = value
        }

    var command: String?
        get() = options.command
        set(value) {
            options.command = value
        }

    var workDir: String?
        get() = options.workDir
        set(value) {
            options.workDir = value
        }

    var parameters: String?
        get() = options.parameters
        set(value) {
            options.parameters = value
        }

    override fun getOptions(): BufRunConfigurationOptions {
        return super.getOptions() as BufRunConfigurationOptions
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                val commandLine = GeneralCommandLine(
                    listOfNotNull(
                        options.bufPath,
                        options.command,
                        *ParametersListUtil.parseToArray(options.parameters ?: "")
                    )
                )
                options.workDir?.let { File(it) }?.takeIf { it.exists() }?.let {
                    commandLine.workDirectory = it
                }
                val processHandler = ProcessHandlerFactory.getInstance()
                    .createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return BufRunConfigurationEditor(project)
    }

    override fun suggestedName(): String? {
        return options.command?.let {
            "buf $it"
        } ?: "buf"
    }
}
