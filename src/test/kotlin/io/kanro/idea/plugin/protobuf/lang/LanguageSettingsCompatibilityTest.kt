package io.kanro.idea.plugin.protobuf.lang

import com.intellij.application.options.CodeStyle
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kanro.idea.plugin.protobuf.lang.formatter.ProtobufCodeStyleSettings
import org.jdom.Element

class LanguageSettingsCompatibilityTest : BasePlatformTestCase() {
    fun testLegacySettingsSurviveWithoutBeingAppliedToNewLanguages() {
        for ((oldId, language) in listOf("protobuf" to ProtobufLanguage, "prototext" to ProtoTextLanguage)) {
            val defaults = CodeStyle.createTestSettings().getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS
            val legacyValue = defaults + 5
            val settings = CodeStyle.createTestSettings()
            settings.readExternal(scheme(oldId to legacyValue))

            assertEquals(defaults, settings.getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS)
            val saved = serialize(settings)
            assertEquals(legacyValue.toString(), blankLinesValue(saved, oldId))

            val reloaded = CodeStyle.createTestSettings()
            reloaded.readExternal(saved)
            assertEquals(defaults, reloaded.getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS)
            assertEquals(legacyValue.toString(), blankLinesValue(serialize(reloaded), oldId))
        }
    }

    fun testLegacySettingsDoNotOverwriteExistingNewLanguageSettings() {
        for ((oldId, language) in listOf("protobuf" to ProtobufLanguage, "prototext" to ProtoTextLanguage)) {
            val defaults = CodeStyle.createTestSettings().getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS
            val legacyValue = defaults + 5
            val newValue = defaults + 10
            val settings = CodeStyle.createTestSettings()
            settings.readExternal(scheme(oldId to legacyValue, language.id to newValue))

            assertEquals(newValue, settings.getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS)
            val saved = serialize(settings)
            assertEquals(legacyValue.toString(), blankLinesValue(saved, oldId))
            assertEquals(newValue.toString(), blankLinesValue(saved, language.id))

            val reloaded = CodeStyle.createTestSettings()
            reloaded.readExternal(saved)
            assertEquals(newValue, reloaded.getCommonSettings(language).BLANK_LINES_AFTER_IMPORTS)
            assertEquals(legacyValue.toString(), blankLinesValue(serialize(reloaded), oldId))
        }
    }

    fun testCustomSettingsKeepTheirExistingStorageTag() {
        val settings = CodeStyle.createTestSettings()
        settings.readExternal(
            Element("code_scheme").addContent(
                Element("ProtobufCodeStyleSettings").addContent(
                    Element("option").setAttribute("name", "BLANK_LINES_AFTER_SYNTAX").setAttribute("value", "3"),
                ),
            ),
        )
        assertEquals(3, settings.getCustomSettings(ProtobufCodeStyleSettings::class.java).BLANK_LINES_AFTER_SYNTAX)

        val saved = serialize(settings)
        assertNotNull(saved.getChild("ProtobufCodeStyleSettings"))
        val reloaded = CodeStyle.createTestSettings()
        reloaded.readExternal(saved)
        assertEquals(3, reloaded.getCustomSettings(ProtobufCodeStyleSettings::class.java).BLANK_LINES_AFTER_SYNTAX)
    }

    private fun scheme(vararg languages: Pair<String, Int>): Element =
        Element("code_scheme").apply {
            for ((id, value) in languages) {
                addContent(
                    Element("codeStyleSettings").setAttribute("language", id).addContent(
                        Element("option")
                            .setAttribute("name", "BLANK_LINES_AFTER_IMPORTS")
                            .setAttribute("value", value.toString()),
                    ),
                )
            }
        }

    private fun serialize(settings: CodeStyleSettings): Element =
        Element("code_scheme").also { settings.writeExternal(it) }

    private fun blankLinesValue(scheme: Element, languageId: String): String? =
        scheme.getChildren("codeStyleSettings")
            .single { it.getAttributeValue("language") == languageId }
            .getChildren("option")
            .single { it.getAttributeValue("name") == "BLANK_LINES_AFTER_IMPORTS" }
            .getAttributeValue("value")
}
