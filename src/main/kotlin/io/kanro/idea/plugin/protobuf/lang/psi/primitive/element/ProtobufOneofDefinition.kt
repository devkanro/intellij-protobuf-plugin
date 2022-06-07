package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufVirtualScope
import javax.swing.Icon

interface ProtobufOneofDefinition : ProtobufVirtualScope, ProtobufDefinition {
    override fun type(): String {
        return "oneof"
    }

    override fun getPresentableText(): String? {
        return "oneof ${name()}"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ONEOF
    }
}
