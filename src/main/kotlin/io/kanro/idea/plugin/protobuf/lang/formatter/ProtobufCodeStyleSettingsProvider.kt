package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage

class ProtobufCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun createCustomSettings(settings: CodeStyleSettings?): CustomCodeStyleSettings? {
        settings ?: return null
        return ProtobufCodeStyleSettings(settings)
    }

    override fun getConfigurableDisplayName(): String {
        return "Protobuf"
    }

    override fun createConfigurable(
        settings: CodeStyleSettings,
        modelSettings: CodeStyleSettings
    ): CodeStyleConfigurable {
        return ProtobufCodeStyleConfigurable(settings, modelSettings, configurableDisplayName)
    }

    class ProtobufCodeStyleConfigurable(
        currentSettings: CodeStyleSettings,
        settings: CodeStyleSettings,
        displayName: String
    ) : CodeStyleAbstractConfigurable(currentSettings, settings, displayName) {
        override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
            return ProtobufCodeStyleMainPanel(currentSettings, settings)
        }
    }

    class ProtobufCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
        TabbedLanguageCodeStylePanel(ProtobufLanguage, currentSettings, settings)
}
