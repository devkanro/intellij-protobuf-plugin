package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufReservedNumber

abstract class ProtobufNumberedScopeDefinitionBase(node: ASTNode) :
    ProtobufScopeDefinitionBase(node),
    ProtobufNumberScope {
    override fun numbered(): Array<ProtobufNumbered> {
        return body()?.findChildren() ?: arrayOf()
    }

    override fun reservedRange(): Array<ProtobufReservedNumber> {
        return arrayOf()
    }
}
