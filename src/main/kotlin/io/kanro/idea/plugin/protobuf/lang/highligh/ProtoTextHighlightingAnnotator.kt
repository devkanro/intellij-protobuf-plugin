package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextNumberValue
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextVisitor

class ProtoTextHighlightingAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        ProtoTextHighlightingAnnotator.annotate(element, holder)
    }

    companion object : Annotator {
        override fun annotate(
            element: PsiElement,
            holder: AnnotationHolder,
        ) {
            element.accept(
                object : ProtoTextVisitor() {
                    override fun visitEnumValue(o: ProtoTextEnumValue) {
                        createHighlight(o, ProtoTextHighlighter.ENUM_VALUE)
                    }

                    override fun visitNumberValue(o: ProtoTextNumberValue) {
                        if (o.floatLiteral == null && o.integerLiteral == null) {
                            createHighlight(o, ProtoTextHighlighter.NUMBER)
                        }
                    }

                    override fun visitFieldName(o: ProtoTextFieldName) {
                        if (o.symbolName != null) {
                            createHighlight(o, ProtoTextHighlighter.FIELD)
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
