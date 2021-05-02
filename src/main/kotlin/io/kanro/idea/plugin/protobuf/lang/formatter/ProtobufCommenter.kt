package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.lang.CodeDocumentationAwareCommenterEx
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufCommenter : CodeDocumentationAwareCommenterEx {
    override fun getBlockCommentPrefix(): String {
        return "/*"
    }

    override fun getBlockCommentSuffix(): String {
        return "*/"
    }

    override fun getLineCommentPrefix(): String {
        return "//"
    }

    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }

    override fun getLineCommentTokenType(): IElementType {
        return ProtobufTokens.LINE_COMMENT
    }

    override fun getBlockCommentTokenType(): IElementType {
        return ProtobufTokens.BLOCK_COMMENT
    }

    override fun getDocumentationCommentTokenType(): IElementType? {
        return null
    }

    override fun getDocumentationCommentPrefix(): String? {
        return null
    }

    override fun getDocumentationCommentLinePrefix(): String? {
        return null
    }

    override fun getDocumentationCommentSuffix(): String? {
        return null
    }

    override fun isDocumentationComment(element: PsiComment?): Boolean {
        return false
    }

    override fun isDocumentationCommentText(element: PsiElement?): Boolean {
        return false
    }
}
