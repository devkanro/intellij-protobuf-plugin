package io.kanro.idea.plugin.protobuf.lang.psi.feature

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.psi.util.parentOfType

interface BodyElement : FoldingElement {
    fun owner(): BodyOwner {
        return parentOfType() ?: throw IllegalStateException()
    }

    override fun folding(): FoldingDescriptor? {
        val start = node.firstChildNode
        val end = node.lastChildNode

        return FoldingDescriptor(
            this,
            start.startOffset,
            end.startOffset + 1,
            null,
            "${start.text}...${end.text}",
        )
    }
}
