package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufFolding

interface ProtobufBody : ProtobufFolding {
    fun owner(): ProtobufBodyOwner {
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
