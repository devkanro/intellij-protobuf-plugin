package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement

class ProtobufEditionAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        val file = (element as? ProtobufElement)?.file() ?: return
        val syntax = file.edition()
        if (syntax != null) return
    }
}
