package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufNumberValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufDocument

class ProtobufHighlightingAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        element.accept(ProtobufHighlightingVisitor(holder))
    }

    private class ProtobufHighlightingVisitor(val holder: AnnotationHolder) : ProtobufVisitor() {
        override fun visitEnumValueDefinition(o: ProtobufEnumValueDefinition) {
            createHighlight(o.identifier() ?: return, ProtobufHighlighter.ENUM_VALUE)
        }

        override fun visitEnumValue(o: ProtobufEnumValue) {
            createHighlight(o, ProtobufHighlighter.ENUM_VALUE)
        }

        override fun visitNumberValue(o: ProtobufNumberValue) {
            if (o.floatLiteral == null && o.integerLiteral == null) {
                createHighlight(o, ProtobufHighlighter.KEYWORD)
            }
        }

        override fun visitPackageName(o: ProtobufPackageName) {
            createHighlight(o, ProtobufHighlighter.IDENTIFIER)
        }

        override fun visitIdentifier(o: ProtobufIdentifier) {
            when (o.parent) {
                is ProtobufMessageDefinition -> createHighlight(o, ProtobufHighlighter.MESSAGE)
                is ProtobufFieldDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                is ProtobufMapFieldDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                is ProtobufOneofDefinition -> createHighlight(o, ProtobufHighlighter.FIELD)
                is ProtobufGroupDefinition -> createHighlight(o, ProtobufHighlighter.MESSAGE)
                is ProtobufFieldAssign -> createHighlight(o, ProtobufHighlighter.FIELD)
                is ProtobufEnumDefinition -> createHighlight(o, ProtobufHighlighter.ENUM)
                is ProtobufServiceDefinition -> createHighlight(o, ProtobufHighlighter.SERVICE)
                is ProtobufRpcDefinition -> createHighlight(o, ProtobufHighlighter.METHOD)
            }
        }

        override fun visitFieldName(o: ProtobufFieldName) {
            createHighlight(o, ProtobufHighlighter.IDENTIFIER)
        }

        override fun visitComment(comment: PsiComment) {
            if (comment is ProtobufDocument) {
                if (comment.owner != null) {
                    createHighlight(comment, ProtobufHighlighter.DOC_COMMENT)
                }
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
    }
}
