package io.kanro.idea.plugin.protobuf.lang.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufFolding
import java.util.Stack

class ProtobufFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val result = mutableListOf<FoldingDescriptor>()
        val stack = Stack<PsiElement>()
        stack.push(root)

        while (stack.isNotEmpty()) {
            val element = stack.pop()
            (element as? ProtobufFolding)?.folding()?.let { result += it }

            element.children.forEach {
                if (it.elementType != TokenType.WHITE_SPACE) stack.push(it)
            }
        }
        return result.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}
