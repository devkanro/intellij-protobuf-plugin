package io.kanro.idea.plugin.protobuf.lang.docs

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufLineCommentImpl
import io.kanro.idea.plugin.protobuf.string.lineCommentRanges
import org.intellij.plugins.markdown.lang.MarkdownLanguage

class ProtobufLineCommentsMarkdownInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is ProtobufLineCommentImpl) return

        val ranges = context.text.lineCommentRanges()
        if (ranges.isEmpty()) return

        registrar.startInjecting(MarkdownLanguage.INSTANCE)
        ranges.forEach {
            registrar.addPlace("", "", context, it)
        }
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(ProtobufLineCommentImpl::class.java)
    }
}