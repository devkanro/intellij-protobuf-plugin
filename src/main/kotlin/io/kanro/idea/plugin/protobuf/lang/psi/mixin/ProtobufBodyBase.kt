package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufBody
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufBodyOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufFolding

abstract class ProtobufBodyBase(node: ASTNode) : ProtobufElementBase(node), ProtobufBody, ProtobufFolding {
    override fun owner(): ProtobufBodyOwner {
        return parent as? ProtobufBodyOwner
            ?: throw IllegalStateException("Protobuf body must be a child node of protobuf body owner")
    }

    override fun folding(): FoldingDescriptor? {
        val start = node.firstChildNode
        val end = node.lastChildNode

        return FoldingDescriptor(
            this,
            start.startOffset,
            end.startOffset + 1,
            null,
            "${start.text}...${end.text}"
        )
    }
}
