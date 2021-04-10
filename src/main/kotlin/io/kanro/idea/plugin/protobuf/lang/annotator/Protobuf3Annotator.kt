package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofBody
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.weak
import io.kanro.idea.plugin.protobuf.lang.support.Options

class Protobuf3Annotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = (element as? ProtobufElement)?.file() ?: return
        if (file.syntax() != "proto3") return

        element.accept(object : ProtobufVisitor() {
            override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                val label = o.fieldLabel ?: return
                if (o.parent is ProtobufOneofBody) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "OneOf field only support none label in proto3."
                    )
                        .range(o.textRange)
                        .create()
                } else {
                    if (label.textMatches("required")) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "'required' field is not supported in proto3."
                        )
                            .range(label.textRange)
                            .create()
                    }
                }
            }

            override fun visitExtendDefinition(o: ProtobufExtendDefinition) {
                val typename = o.typeName ?: return
                val name = (typename.resolve() as? ProtobufMessageDefinition)?.qualifiedName() ?: return
                if (Options.all.contains(name)) {
                    return
                }
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Only Options can be extended in proto3."
                )
                    .range(typename.textRange)
                    .create()
            }

            override fun visitGroupDefinition(o: ProtobufGroupDefinition) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'group' is not supported in proto3."
                )
                    .range(o.textRange)
                    .create()
            }

            override fun visitExtensionStatement(o: ProtobufExtensionStatement) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'extension' is not supported in proto3."
                )
                    .range(o.textRange)
                    .create()
            }

            override fun visitImportStatement(o: ProtobufImportStatement) {
                if (o.weak()) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "'weak' import is not supported in proto3."
                    )
                        .range(o.importLabel?.textRange ?: o.textRange)
                        .create()
                }
            }
        })
    }
}
