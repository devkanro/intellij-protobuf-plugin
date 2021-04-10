package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
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
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufKeywordToken
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufToken
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

class ProtobufHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(ProtobufHighlightingVisitor(holder))
    }

    private class ProtobufHighlightingVisitor(val holder: AnnotationHolder) : ProtobufVisitor() {
        override fun visitEnumValueDefinition(o: ProtobufEnumValueDefinition) {
            createHighlight(o.identifier() ?: return, ProtobufHighlighter.ENUM_VALUE)
        }

        override fun visitNumberValue(o: ProtobufNumberValue) {
            if (o.floatLiteral == null && o.integerLiteral == null) {
                createHighlight(o, ProtobufHighlighter.KEYWORD)
            }
        }

        override fun visitSymbolName(o: ProtobufSymbolName) {
            if (o.prevSibling == null && o.nextSibling == null) {
                if (BuiltInType.isBuiltInType(o.text)) {
                    createHighlight(o, ProtobufHighlighter.KEYWORD)
                    return
                }
            }
            createHighlight(o, ProtobufHighlighter.IDENTIFIER)
        }

        override fun visitPackageName(o: ProtobufPackageName) {
            createHighlight(o, ProtobufHighlighter.IDENTIFIER)
        }

        override fun visitIdentifier(o: ProtobufIdentifier) {
            when (o.parent) {
                is ProtobufMessageDefinition,
                is ProtobufFieldDefinition,
                is ProtobufMapFieldDefinition,
                is ProtobufOneofDefinition,
                is ProtobufGroupDefinition,
                is ProtobufFieldAssign,
                is ProtobufEnumDefinition,
                is ProtobufServiceDefinition,
                is ProtobufRpcDefinition -> {
                    createHighlight(o, ProtobufHighlighter.IDENTIFIER)
                }
            }
        }

        override fun visitFieldName(o: ProtobufFieldName) {
            createHighlight(o, ProtobufHighlighter.IDENTIFIER)
        }

        override fun visitElement(o: ProtobufElement) {
            when (o.node.elementType) {
                is ProtobufToken, is ProtobufKeywordToken -> {
                    o
                }
            }
        }

        private fun createHighlight(element: PsiElement, textAttributesKey: TextAttributesKey) {
            holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                .range(element.textRange)
                .textAttributes(textAttributesKey)
                .create()
        }
    }
}
