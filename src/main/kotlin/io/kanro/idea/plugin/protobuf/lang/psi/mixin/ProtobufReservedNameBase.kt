package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName

abstract class ProtobufReservedNameBase(node: ASTNode) :
    ProtobufElementBase(node),
    ProtobufReservedName {
    override fun getName(): String? {
        return identifierLiteral?.text
    }
}
