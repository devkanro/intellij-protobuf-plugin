package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumbered
import javax.swing.Icon

interface ProtobufEnumValueDefinition : ProtobufNumbered {
    override fun owner(): ProtobufEnumDefinition? {
        return parentOfType()
    }

    override fun type(): String {
        return "enum value"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ENUM_VALUE
    }

    override fun tailText(): String? {
        return " = ${number()}"
    }
}
