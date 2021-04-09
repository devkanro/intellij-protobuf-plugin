package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import java.lang.IllegalStateException

abstract class ProtobufIdentifierBase(node: ASTNode) :
    ProtobufElementBase(node),
    ProtobufIdentifier {
    override fun owner(): ProtobufDefinition {
        return parent as? ProtobufDefinition ?: throw IllegalStateException("Parent of ProtobufIdentifier must be ProtobufDefinition")
    }
}
