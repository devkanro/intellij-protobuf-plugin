package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import javax.swing.Icon

abstract class ProtobufEnumBase(node: ASTNode) :
    ProtobufReservableScopeDefinitionBase(node),
    ProtobufEnumDefinition {
    override fun type(): String {
        return "enum"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ENUM
    }
}
