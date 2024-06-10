package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import javax.swing.Icon

class ProtobufColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Protobuf"
    }

    override fun getIcon(): Icon {
        return ProtobufIcons.FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return ProtobufHighlighter()
    }

    override fun getDemoText(): String {
        return """
            /*
             * Block Comments
             */
            syntax = "proto3";

            package protobuf_unittest; // Line Comment

            import "google/protobuf/any.proto";

            option java_outer_classname = "TestAnyProto";

            // Doc Comment
            message TestMessage {
                int32 int32_value = 1 [default = 10086];
                google.protobuf.Any any_value = 2;
                repeated google.protobuf.Any repeated_any_value = 3;
                string text = 4 [default = "test_default"];
            }
            
            enum TestEnum {
                UNKNOWN = 0,
                VALUE = 1
            }
            
            service TestService {
                rpc TestMethod(TestMessage) returns (google.protobuf.Empty) {
                    option (google.api.http) = {
                        post: "/v1/test"
                        body: "*"
                    };
                }
            }
            """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? {
        return mutableMapOf()
    }

    companion object {
        val DESCRIPTORS =
            arrayOf(
                AttributesDescriptor("Braces and Operators//Braces", ProtobufHighlighter.BRACES),
                AttributesDescriptor("Braces and Operators//Brackets", ProtobufHighlighter.BRACKETS),
                AttributesDescriptor("Braces and Operators//Comma", ProtobufHighlighter.COMMA),
                AttributesDescriptor("Braces and Operators//Dot", ProtobufHighlighter.DOT),
                AttributesDescriptor("Braces and Operators//Operator sign", ProtobufHighlighter.OPERATION_SIGN),
                AttributesDescriptor("Braces and Operators//Parentheses", ProtobufHighlighter.PARENTHESES),
                AttributesDescriptor("Braces and Operators//Semicolon", ProtobufHighlighter.SEMICOLON),
                AttributesDescriptor("Comments//Block comment", ProtobufHighlighter.BLOCK_COMMENT),
                AttributesDescriptor("Comments//Line comment", ProtobufHighlighter.LINE_COMMENT),
                AttributesDescriptor("Comments//Doc comment", ProtobufHighlighter.DOC_COMMENT),
                AttributesDescriptor("Identifiers//Default", ProtobufHighlighter.IDENTIFIER),
                AttributesDescriptor("Identifiers//Message", ProtobufHighlighter.MESSAGE),
                AttributesDescriptor("Identifiers//Field", ProtobufHighlighter.FIELD),
                AttributesDescriptor("Identifiers//Enum", ProtobufHighlighter.ENUM),
                AttributesDescriptor("Identifiers//Enum value", ProtobufHighlighter.ENUM_VALUE),
                AttributesDescriptor("Identifiers//Service", ProtobufHighlighter.SERVICE),
                AttributesDescriptor("Identifiers//Method", ProtobufHighlighter.METHOD),
                AttributesDescriptor("Keyword", ProtobufHighlighter.KEYWORD),
                AttributesDescriptor("Number", ProtobufHighlighter.NUMBER),
                AttributesDescriptor("String", ProtobufHighlighter.STRING),
            )
    }
}
