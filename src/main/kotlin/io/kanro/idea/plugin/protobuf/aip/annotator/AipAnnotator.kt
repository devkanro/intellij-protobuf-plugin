package io.kanro.idea.plugin.protobuf.aip.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.aip.reference.AipResourceReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufFieldReferenceInString
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufRpcInputFieldReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufRpcOutputFieldReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufTypeNameInStringReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.value

class AipAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(object : ProtobufVisitor() {
            override fun visitStringValue(o: ProtobufStringValue) {
                o.reference?.let {
                    if (it.resolve() == null) {
                        when (it) {
                            is AipResourceReference -> {
                                holder.newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Resource name ${o.text} not found."
                                )
                                    .range(o.textRange)
                                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                                    .create()
                            }
                            is ProtobufRpcInputFieldReference,
                            is ProtobufRpcOutputFieldReference -> {
                                holder.newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Field ${o.text} of message \"${
                                    (it as ProtobufFieldReferenceInString).message()?.qualifiedName()
                                    }\" not found."
                                )
                                    .range(o.textRange)
                                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                                    .create()
                            }
                            is ProtobufTypeNameInStringReference -> {
                                holder.newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Type \"${o.value()}\" not found."
                                )
                                    .range(o.textRange)
                                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                                    .create()
                            }
                        }
                    }
                    return
                }
            }
        })
    }
}
