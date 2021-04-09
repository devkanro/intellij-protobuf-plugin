package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinitionContributor
import javax.swing.Icon

abstract class ProtobufMessageBase(node: ASTNode) :
    ProtobufReservableScopeWithExtensionDefinitionBase(node),
    ProtobufMessageDefinition {
    override fun type(): String {
        return "message"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.MESSAGE
    }

    override fun definitions(): Array<ProtobufDefinition> {
        val contributedDefinitions = this.body()?.findChildren<ProtobufDefinitionContributor>()?.flatMap {
            it.definitions().asIterable()
        }?.toTypedArray() ?: arrayOf()
        return super.definitions() + contributedDefinitions
    }
}
