package io.kanro.idea.plugin.protobuf.lang

import com.intellij.openapi.util.JDOMUtil
import junit.framework.TestCase
import org.jdom.Element
import java.io.File

class LanguageDescriptorTest : TestCase() {
    fun testAllDescriptorsStopRegisteringOldLanguageIds() {
        val main = descriptor("plugin.xml")
        val descriptors = listOf(main) + main.getChildren("depends").mapNotNull {
            it.getAttributeValue("config-file")?.let(::descriptor)
        }

        for (descriptor in descriptors) {
            for (extension in extensions(descriptor)) {
                val language = extension.getAttributeValue("language")
                assertFalse(extension.toString(), language == "protobuf" || language == "prototext")
            }
        }
    }

    fun testFileTypeRegistrationsPreserveNamesAndExtensions() {
        val fileTypes = extensions(descriptor("plugin.xml")).filter { it.name == "fileType" }
        val protobuf = fileTypes.single { it.getAttributeValue("name") == "protobuf" }
        val prototext = fileTypes.single { it.getAttributeValue("name") == "prototext" }

        assertEquals("protocol_buffers", protobuf.getAttributeValue("language"))
        assertEquals("protocol_buffers_text", prototext.getAttributeValue("language"))
        assertEquals("proto", protobuf.getAttributeValue("extensions"))
        assertEquals("txtpb;pbtxt;textproto;textpb;protoascii;pb.txt", prototext.getAttributeValue("extensions"))
    }

    fun testOptionalLineMarkersUseNewLanguageId() {
        val providers = mapOf(
            "io.kanro.idea.plugin.protobuf-java.xml" to listOf(
                "io.kanro.idea.plugin.protobuf.java.ProtobufLineMarkerProvider",
            ),
            "io.kanro.idea.plugin.protobuf-sisyphus.xml" to listOf(
                "io.kanro.idea.plugin.protobuf.sisyphus.SisyphusProtobufLineMarkerProvider",
            ),
            "io.kanro.idea.plugin.protobuf-client.xml" to listOf(
                "io.kanro.idea.plugin.protobuf.grpc.gutter.GrpcRunRequestGutterProvider",
                "io.kanro.idea.plugin.protobuf.grpc.gutter.AipRunRequestGutterProvider",
            ),
        )

        for ((file, implementations) in providers) {
            val extensions = extensions(descriptor(file))
            for (implementation in implementations) {
                val provider = extensions.single { it.getAttributeValue("implementationClass") == implementation }
                assertEquals("codeInsight.lineMarkerProvider", provider.name)
                assertEquals(implementation, "protocol_buffers", provider.getAttributeValue("language"))
            }
        }
    }

    private fun descriptor(name: String): Element =
        JDOMUtil.load(File("src/main/resources/META-INF", name))

    private fun extensions(descriptor: Element): List<Element> =
        descriptor.getChildren("extensions").flatMap { it.children }
}
