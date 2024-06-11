package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.QualifiedNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import java.util.function.Consumer

class ProtobufDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(
        element: PsiElement?,
        originalElement: PsiElement?,
    ): String? {
        (originalElement as? DocumentOwner)?.navigateInfo()?.let { return it }
        (element as? DocumentOwner)?.navigateInfo()?.let { return it }
        return null
    }

    override fun generateDoc(
        element: PsiElement?,
        originalElement: PsiElement?,
    ): String? {
        (originalElement as? DocumentOwner)?.document()?.let { return it }
        (element as? DocumentOwner)?.document()?.let { return it }
        return null
    }

    override fun generateHoverDoc(
        element: PsiElement,
        originalElement: PsiElement?,
    ): String? {
        (originalElement as? DocumentOwner)?.hoverDocument()?.let { return it }
        (element as? DocumentOwner)?.hoverDocument()?.let { return it }
        return null
    }

    override fun getDocumentationElementForLink(
        psiManager: PsiManager?,
        link: String,
        context: PsiElement,
    ): PsiElement? {
        return ProtobufSymbolResolver.resolveRelatively(context as ProtobufElement, link.toQualifiedName())
            ?: StubIndex.getElements(
                QualifiedNameIndex.key,
                link,
                context.project,
                ProtobufRootResolver.searchScope(context),
                ProtobufElement::class.java,
            ).firstOrNull()
    }

    override fun generateRenderedDoc(comment: PsiDocCommentBase): String? {
        return (comment as? DocumentElement)?.render()
    }

    override fun collectDocComments(
        file: PsiFile,
        sink: Consumer<in PsiDocCommentBase>,
    ) {
        file.walkChildren<DocumentElement> {
            if (it.owner != null) {
                sink.accept(it)
            }
        }
    }
}
