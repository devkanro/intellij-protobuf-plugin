package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufBody
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufBodyOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufScope

abstract class ProtobufLeafDefinitionBase(node: ASTNode) :
    ProtobufDefinitionBase(node),
    ProtobufOptionOwner,
    ProtobufBodyOwner {
    override fun body(): ProtobufBody? {
        return findChildByClass(ProtobufBody::class.java)
    }

    override fun options(): Array<ProtobufOptionAssign> {
        return body()?.findChildren<ProtobufOptionHover>()?.mapNotNull {
            it.option()
        }?.toTypedArray() ?: arrayOf()
    }
}

abstract class ProtobufScopeDefinitionBase(node: ASTNode) :
    ProtobufLeafDefinitionBase(node),
    ProtobufScope,
    ProtobufOptionOwner,
    ProtobufBodyOwner {
    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun definitions(): Array<ProtobufDefinition> {
        return body()?.findChildren() ?: arrayOf()
    }

    override fun reservedNames(): Array<ProtobufReservedName> {
        return arrayOf()
    }
}
