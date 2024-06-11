package io.kanro.idea.plugin.protobuf.lang.folding

import com.intellij.codeInsight.folding.CodeFoldingSettings
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import io.kanro.idea.plugin.protobuf.lang.psi.feature.FoldingElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren
import java.util.Stack

class ProtobufFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(
        root: PsiElement,
        document: Document,
        quick: Boolean,
    ): Array<FoldingDescriptor> {
        val result = mutableListOf<FoldingDescriptor>()
        val file = (root.containingFile as? ProtobufFile) ?: return arrayOf()
        result += buildFoldingDescriptorForFile(file)

        root.walkChildren<FoldingElement> {
            it.folding()?.let { result += it }
        }
        return result.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }

    private fun buildFoldingDescriptorForFile(protobufFile: ProtobufFile): List<FoldingDescriptor> {
        val result = mutableListOf<FoldingDescriptor>()
        val stack = Stack<ProtobufImportStatement>()
        val default = CodeFoldingSettings.getInstance().COLLAPSE_IMPORTS

        protobufFile.children.forEach {
            when (it) {
                is ProtobufImportStatement -> stack.push(it)
                is PsiWhiteSpace,
                is PsiComment,
                -> {
                }

                else -> {
                    if (stack.size >= 2) {
                        val start = stack.firstElement()
                        val end = stack.lastElement()
                        stack.clear()
                        val range =
                            TextRange.create(
                                start.stringValue?.textRange?.startOffset ?: return@forEach,
                                end.textRange.startOffset + end.textLength,
                            )

                        result +=
                            FoldingDescriptor(
                                protobufFile.node,
                                range,
                                FoldingGroup.newGroup("import"),
                                "...",
                                default,
                                setOf(),
                            )
                    } else {
                        stack.clear()
                    }
                }
            }
        }

        return result
    }
}
