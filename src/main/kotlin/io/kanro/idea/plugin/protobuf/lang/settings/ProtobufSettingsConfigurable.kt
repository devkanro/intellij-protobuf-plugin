package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.ConfigurableBase
import com.intellij.openapi.project.Project

class ProtobufSettingsConfigurable(val project: Project) : ConfigurableBase<ProtobufSettingsComponent, ProtobufSettings>(
    "protobuf.language", "Protobuf", null
) {
    override fun getSettings(): ProtobufSettings {
        return ServiceManager.getService(project, ProtobufSettings::class.java)
    }

    override fun createUi(): ProtobufSettingsComponent {
        return ProtobufSettingsComponent(project)
    }
}
