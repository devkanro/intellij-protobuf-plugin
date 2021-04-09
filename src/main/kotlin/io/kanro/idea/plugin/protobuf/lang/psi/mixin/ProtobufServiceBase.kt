package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import javax.swing.Icon

abstract class ProtobufServiceBase(node: ASTNode) : ProtobufScopeDefinitionBase(node), ProtobufServiceDefinition {
    override fun type(): String {
        return "service"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.SERVICE
    }

    override fun getLocationString(): String? {
        return owner()?.scope()?.toString()
    }
}
