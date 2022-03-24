package io.kanro.idea.plugin.protobuf.buf.run

import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.util.xmlb.annotations.OptionTag

class BufRunConfigurationOptions(
    bufPath: String? = null,
    command: String? = null,
    workDir: String? = null,
    parameters: String? = null
) : LocatableRunConfigurationOptions() {
    @get:OptionTag("bufPath")
    var bufPath: String? by string(bufPath)

    @get:OptionTag("command")
    var command: String? by string(command)

    @get:OptionTag("workDir")
    var workDir: String? by string(workDir)

    @get:OptionTag("parameters")
    var parameters: String? by string(parameters)
}
