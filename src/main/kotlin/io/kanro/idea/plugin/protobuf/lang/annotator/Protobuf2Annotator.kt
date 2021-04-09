package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneOfBody
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

class Protobuf2Annotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = (element as? ProtobufElement)?.file() ?: return
        val syntax = file.syntax()
        if (syntax != null && syntax != "proto2") return

        element.accept(object : ProtobufVisitor() {
            override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                if (o.parent is ProtobufOneOfBody) {
                    if (o.fieldLabel?.textMatches("optional") == false) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "OneOf file only support 'optional' or none label in proto2."
                        )
                            .range(o.textRange)
                            .create()
                    }
                } else {
                    if (o.fieldLabel == null) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field must has label in proto2."
                        )
                            .range(o.textRange)
                            .create()
                    }
                }
            }

            override fun visitGroupField(o: ProtobufGroupField) {
                val name = o.qualifiedName()?.lastComponent ?: return
                if (name.isEmpty() && !name[0].isUpperCase()) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "'group' field name must start with a capital letter."
                    )
                        .range(o.textRange)
                        .create()
                }
            }
        })
    }
}
