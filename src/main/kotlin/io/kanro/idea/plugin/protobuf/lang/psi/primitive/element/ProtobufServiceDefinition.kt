package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import javax.swing.Icon

interface ProtobufServiceDefinition : ProtobufScope, ProtobufDefinition {
    override fun type(): String {
        return "service"
    }
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.SERVICE
    }
}
