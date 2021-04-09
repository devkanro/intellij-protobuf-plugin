package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionHover

abstract class ProtobufOptionHoverBase(node: ASTNode) : ProtobufElementBase(node), ProtobufOptionHover {
    override fun option(): ProtobufOptionAssign? {
        return findChildByClass(ProtobufOptionAssign::class.java)
    }
}
