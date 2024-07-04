package io.kanro.idea.plugin.protobuf.lang.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufNumberValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufVisitor

class ProtobufHighlightingAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        ProtobufHighlightingAnnotator.annotate(element, holder)
    }

    companion object : Annotator {
        override fun annotate(
            element: PsiElement,
            holder: AnnotationHolder,
        ) {
            element.accept(
                object : ProtobufVisitor() {
                    override fun visitEnumValueDefinition(o: ProtobufEnumValueDefinition) {
                        createHighlight(o.identifier() ?: return, ProtobufHighlighter.ENUM_VALUE)
                    }

                    override fun visitPackageName(o: ProtobufPackageName) {
                        createHighlight(o, ProtobufHighlighter.IDENTIFIER)
                    }

                    override fun visitOptionName(o: ProtobufOptionName) {
                        createHighlight(o, ProtobufHighlighter.IDENTIFIER)
                    }

                    override fun visitIdentifier(o: ProtobufIdentifier) {
                        when (o.parent) {
                            is ProtobufMessageDefinition -> createHighlight(o, ProtobufHighlighter.MESSAGE)
                            is ProtobufFieldDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                            is ProtobufMapFieldDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                            is ProtobufOneofDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                            is ProtobufGroupDefinition -> createHighlight(o, ProtobufHighlighter.MESSAGE)
                            is ProtobufEnumDefinition -> createHighlight(o, ProtobufHighlighter.ENUM)
                            is ProtobufServiceDefinition -> createHighlight(o, ProtobufHighlighter.SERVICE)
                            is ProtobufRpcDefinition -> createHighlight(o, ProtobufHighlighter.METHOD)
                        }
                    }

                    override fun visitComment(comment: PsiComment) {
                        if (comment is DocumentElement) {
                            if (comment.owner != null) {
                                createHighlight(comment, ProtobufHighlighter.DOC_COMMENT)
                            }
                        }
                    }

                    override fun visitEnumValue(o: ProtobufEnumValue) {
                        createHighlight(o, ProtobufHighlighter.ENUM_VALUE)
                    }

                    override fun visitNumberValue(o: ProtobufNumberValue) {
                        if (o.floatLiteral == null && o.integerLiteral == null) {
                            createHighlight(o, ProtobufHighlighter.KEYWORD)
                        }
                    }

                    override fun visitExtensionFieldName(o: ProtobufExtensionFieldName) {
                        if (o.extensionFieldName == null) {
                            createHighlight(o.symbolName, ProtobufHighlighter.FIELD)
                        }
                    }

                    private fun createHighlight(
                        element: PsiElement,
                        textAttributesKey: TextAttributesKey,
                    ) {
                        holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                            .range(element.textRange)
                            .textAttributes(textAttributesKey)
                            .create()
                    }
                },
            )
        }
    }
}
