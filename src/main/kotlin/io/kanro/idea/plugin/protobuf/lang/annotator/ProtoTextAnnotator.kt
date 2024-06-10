package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElement
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.text.enum
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderFileReference
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderMessageReference
import io.kanro.idea.plugin.protobuf.lang.psi.text.resolve

class ProtoTextAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        if (element !is ProtoTextElement) {
            return
        }
        element.accept(
            object : ProtoTextVisitor() {
                override fun visitComment(comment: PsiComment) {
                    when (val reference = comment.reference) {
                        is ProtoTextHeaderFileReference -> {
                            if (reference.resolve() == null) {
                                holder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Schema file '${reference.fileName()}' not found",
                                ).range(reference.absoluteRange).highlightType(ProblemHighlightType.WARNING).create()
                            }
                        }

                        is ProtoTextHeaderMessageReference -> {
                            if (reference.resolve() == null) {
                                holder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Message '${reference.messageName()}' not found",
                                ).range(reference.absoluteRange).highlightType(ProblemHighlightType.WARNING).create()
                            }
                        }
                    }
                }

                override fun visitEnumValue(o: ProtoTextEnumValue) {
                    if (o.file()?.schema() == null) return

                    val enum = o.enum() ?: return
                    enum.items<ProtobufEnumValueDefinition> {
                        if (it.name() == o.text) {
                            return
                        }
                    }
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Enum value '${o.text}' not found",
                    ).range(o.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL).create()
                }

                override fun visitFieldName(o: ProtoTextFieldName) {
                    if (o.file()?.schema() == null) return

                    if (o.resolve() == null) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field '${o.text}' not existed",
                        ).range(o.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL).create()
                    }
                }
            },
        )
    }
}
