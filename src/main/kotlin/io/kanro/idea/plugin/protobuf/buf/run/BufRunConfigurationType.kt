package io.kanro.idea.plugin.protobuf.buf.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import io.kanro.idea.plugin.protobuf.Icons

class BufRunConfigurationType : ConfigurationTypeBase(
    "buf", "Buf Command", "Run buf commands", Icons.BUF_LOGO
) {
    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(BufRunConfigurationFactory())
    }

    companion object {
        val INSTANCE by lazy {
            ConfigurationType.CONFIGURATION_TYPE_EP.findExtension(BufRunConfigurationType::class.java)!!
        }
    }
}
