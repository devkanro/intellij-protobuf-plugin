package io.kanro.idea.plugin.protobuf.lang

import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile

class LanguageRegistrationTest : BasePlatformTestCase() {
    fun testLanguageIdsDoNotClaimBundledPluginIds() {
        assertEquals("protocol_buffers", ProtobufLanguage.id)
        assertEquals("protocol_buffers_text", ProtoTextLanguage.id)
        assertSame(ProtobufLanguage, Language.findLanguageByID("protocol_buffers"))
        assertSame(ProtoTextLanguage, Language.findLanguageByID("protocol_buffers_text"))
        assertNotSame(ProtobufLanguage, Language.findLanguageByID("protobuf"))
        assertNotSame(ProtoTextLanguage, Language.findLanguageByID("prototext"))
        assertEquals("protobuf", ProtobufLanguage.displayName)
        assertEquals("prototext", ProtoTextLanguage.displayName)
    }

    fun testFileTypeNamesRemainStable() {
        val fileTypes = FileTypeManager.getInstance()
        val protobuf = fileTypes.findFileTypeByName("protobuf")
        val prototext = fileTypes.findFileTypeByName("prototext")

        assertTrue(protobuf is ProtobufFileType)
        assertTrue(prototext is ProtoTextFileType)
        assertEquals("protobuf", ProtobufFileType.INSTANCE.name)
        assertEquals("prototext", ProtoTextFileType.INSTANCE.name)
        assertEquals("proto", protobuf!!.defaultExtension)
        assertEquals("txtpb", prototext!!.defaultExtension)
        assertEquals("Protocol Buffer", protobuf.description)
        assertEquals("Protocol Buffer Text Format", prototext.description)
        assertSame(ProtobufLanguage, (protobuf as ProtobufFileType).language)
        assertSame(ProtoTextLanguage, (prototext as ProtoTextFileType).language)
    }

    fun testProtobufParserBinding() {
        assertTrue(LanguageParserDefinitions.INSTANCE.forLanguage(ProtobufLanguage) is ProtobufParserDefinition)
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "sample.proto",
            ProtobufFileType.INSTANCE,
            """syntax = "proto3"; message Sample { string name = 1; }""",
        )

        assertTrue(file is ProtobufFile)
        assertSame(ProtobufLanguage, file.language)
        assertNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
    }

    fun testProtoTextParserBinding() {
        assertTrue(LanguageParserDefinitions.INSTANCE.forLanguage(ProtoTextLanguage) is ProtoTextParserDefinition)
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "sample.txtpb",
            ProtoTextFileType.INSTANCE,
            """name: "sample" nested { value: 1 }""",
        )

        assertTrue(file is ProtoTextFile)
        assertSame(ProtoTextLanguage, file.language)
        assertNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
    }

    fun testCodeStyleProvidersUseNewLanguages() {
        assertSame(ProtobufLanguage, LanguageCodeStyleSettingsProvider.forLanguage(ProtobufLanguage)?.language)
        assertSame(ProtoTextLanguage, LanguageCodeStyleSettingsProvider.forLanguage(ProtoTextLanguage)?.language)
    }
}
