package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.openapi.options.ConfigurableBase
import com.intellij.openapi.project.Project

class BufSettingsConfigurable(val project: Project) :
    ConfigurableBase<BufSettingsComponent, BufSettings>(
        "protobuf.buf", "Buf", null
    ) {
    override fun getSettings(): BufSettings {
        return project.getService(BufSettings::class.java)
    }

    override fun createUi(): BufSettingsComponent {
        return BufSettingsComponent(project)
    }
}
