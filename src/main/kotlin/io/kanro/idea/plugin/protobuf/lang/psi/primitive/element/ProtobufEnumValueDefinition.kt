package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
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
        return ProtobufIcons.ENUM_VALUE
    }

    override fun tailText(): String? {
        return " = ${number()}"
    }
}
