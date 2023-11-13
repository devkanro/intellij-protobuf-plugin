package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.type.ProtobufBlock
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.type.ProtobufStatement
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufCommentToken
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

enum class BlockType {
    BODY,
    STATEMENT,
    FRAGMENT,
}

class ProtobufBlock(
    val type: BlockType,
    node: ASTNode,
    warp: Wrap?,
    alignment: Alignment?,
    protected val spacingBuilder: SpacingBuilder,
) : AbstractBlock(
        node,
        warp,
        alignment,
    ) {
    override fun getSpacing(
        child1: Block?,
        child2: Block,
    ): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean {
        return node.firstChildNode == null
    }

    override fun getIndent(): Indent {
        return when (type) {
            BlockType.BODY -> Indent.getNoneIndent()
            BlockType.STATEMENT -> {
                if (node.treeParent.psi is ProtobufFile) {
                    Indent.getNoneIndent()
                } else {
                    Indent.getNormalIndent()
                }
            }
            BlockType.FRAGMENT -> {
                if (node.treePrev == null) return Indent.getNoneIndent()
                when (node.elementType) {
                    ProtobufTokens.SEMI,
                    ProtobufTokens.COMMA,
                    ProtobufTokens.LBRACE,
                    ProtobufTokens.RBRACE,
                    ProtobufTokens.LPAREN,
                    ProtobufTokens.RPAREN,
                    ProtobufTokens.LBRACK,
                    ProtobufTokens.RBRACK,
                    ProtobufTokens.LT,
                    ProtobufTokens.GT,
                    -> Indent.getNoneIndent()
                    else -> Indent.getContinuationWithoutFirstIndent()
                }
            }
        }
    }

    override fun getChildIndent(): Indent? {
        return when (type) {
            BlockType.BODY -> {
                if (node.psi is ProtobufFile) {
                    Indent.getNoneIndent()
                } else {
                    Indent.getNormalIndent()
                }
            }
            BlockType.STATEMENT -> Indent.getContinuationWithoutFirstIndent()
            BlockType.FRAGMENT -> Indent.getContinuationWithoutFirstIndent()
        }
    }

    override fun buildChildren(): List<Block> {
        var child: ASTNode? = node.firstChildNode ?: return listOf()
        val result = mutableListOf<Block>()
        while (child != null) {
            if (child.elementType == TokenType.WHITE_SPACE) {
                child = child.treeNext
                continue
            }
            result += buildChild(child)
            child = child.treeNext
        }
        return result
    }

    private fun buildChild(child: ASTNode): Block {
        val psi = child.psi
        if (psi is ProtobufBlock) {
            return ProtobufBlock(BlockType.BODY, child, wrap, alignment, spacingBuilder)
        }
        if (psi is ProtobufStatement) {
            return ProtobufBlock(BlockType.STATEMENT, child, wrap, alignment, spacingBuilder)
        }
        if (child.elementType is ProtobufCommentToken) {
            return ProtobufBlock(BlockType.STATEMENT, child, wrap, alignment, spacingBuilder)
        }
        return ProtobufBlock(BlockType.FRAGMENT, child, wrap, alignment, spacingBuilder)
    }

    override fun toString(): String {
        return "[$type] ${super.toString()}"
    }
}
