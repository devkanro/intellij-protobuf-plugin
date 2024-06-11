package io.kanro.idea.plugin.protobuf.lang.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import javax.swing.Icon

class ProtoTextColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "ProtoText"
    }

    override fun getIcon(): Icon {
        return ProtobufIcons.TEXT_FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return ProtoTextHighlighter()
    }

    override fun getDemoText(): String {
        return """
            # proto-file: google/protobuf/unittest_custom_options.proto
            # proto-message: Aggregate
            
            s: 'FileAnnotation'
            i: 100
            sub { s: 'NestedFileAnnotation' }
            file {
              [protobuf_unittest.fileopt] { s: 'FileExtensionAnnotation' }
            }
            mset {
              [protobuf_unittest.AggregateMessageSetElement.message_set_extension] {
                s: 'EmbeddedMessageSetElement'
              }
            }
            any {
              [type.googleapis.com/protobuf_unittest.AggregateMessageSetElement] {
                s: 'EmbeddedMessageSetElement'
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
                AttributesDescriptor("Braces and Operators//Braces", ProtoTextHighlighter.BRACES),
                AttributesDescriptor("Braces and Operators//Brackets", ProtoTextHighlighter.BRACKETS),
                AttributesDescriptor("Braces and Operators//Comma", ProtoTextHighlighter.COMMA),
                AttributesDescriptor("Braces and Operators//Dot", ProtoTextHighlighter.DOT),
                AttributesDescriptor("Braces and Operators//Operator sign", ProtoTextHighlighter.OPERATION_SIGN),
                AttributesDescriptor("Braces and Operators//Parentheses", ProtoTextHighlighter.PARENTHESES),
                AttributesDescriptor("Braces and Operators//Semicolon", ProtoTextHighlighter.SEMICOLON),
                AttributesDescriptor("Comments//Block comment", ProtoTextHighlighter.BLOCK_COMMENT),
                AttributesDescriptor("Comments//Line comment", ProtoTextHighlighter.LINE_COMMENT),
                AttributesDescriptor("Identifiers//Default", ProtoTextHighlighter.IDENTIFIER),
                AttributesDescriptor("Identifiers//Field", ProtoTextHighlighter.FIELD),
                AttributesDescriptor("Identifiers//Enum value", ProtoTextHighlighter.ENUM_VALUE),
                AttributesDescriptor("Keyword", ProtoTextHighlighter.KEYWORD),
                AttributesDescriptor("Number", ProtoTextHighlighter.NUMBER),
                AttributesDescriptor("String", ProtoTextHighlighter.STRING),
            )
    }
}
