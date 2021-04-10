package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumbered
import javax.swing.Icon

interface ProtobufEnumValueDefinition : ProtobufNumbered {
    @JvmDefault
    override fun type(): String {
        return "enum value"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ENUM_VALUE
    }

    @JvmDefault
    override fun tailText(): String? {
        return " = ${number()}"
    }
}
