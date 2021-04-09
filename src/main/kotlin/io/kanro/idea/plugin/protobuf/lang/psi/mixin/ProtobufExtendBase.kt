package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufBody
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition

abstract class ProtobufExtendBase(node: ASTNode) : ProtobufElementBase(node), ProtobufExtendDefinition {
    override fun body(): ProtobufBody? {
        return findChildByClass(ProtobufBody::class.java)
    }

    override fun definitions(): Array<ProtobufDefinition> {
        return body()?.findChildren() ?: arrayOf()
    }
}
