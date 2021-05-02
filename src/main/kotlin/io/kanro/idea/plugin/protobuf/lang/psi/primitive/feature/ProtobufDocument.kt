package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition

interface ProtobufDocument : PsiDocCommentBase {
    @JvmDefault
    override fun getOwner(): PsiElement? {
        var next = nextSibling
        var newLine = 0
        while (next != null) {
            if (newLine > 1) return null
            if (next is ProtobufDefinition) return next
            if (next is PsiWhiteSpace) {
                newLine += next.text.count { it == '\n' }
                next = next.nextSibling
                continue
            }
            return null
        }
        return null
    }

    fun render(): String
}
