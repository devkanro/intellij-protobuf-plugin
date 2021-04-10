package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import javax.swing.Icon

interface ProtobufEnumDefinition : ProtobufNumberScope, ProtobufDefinition {
    @JvmDefault
    override fun type(): String {
        return "enum"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ENUM
    }
}
