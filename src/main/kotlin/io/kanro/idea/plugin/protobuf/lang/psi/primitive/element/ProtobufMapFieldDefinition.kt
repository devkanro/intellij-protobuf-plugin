package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import javax.swing.Icon

interface ProtobufMapFieldDefinition : ProtobufFieldLike {
    @JvmDefault
    override fun type(): String {
        return "field"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }

    @JvmDefault
    override fun fieldType(): String? {
        val typeNames = findChildren<ProtobufTypeName>()
        if (typeNames.size != 2) return "map"
        val key = typeNames[0].symbolNameList.lastOrNull()?.text ?: return "map"
        val value = typeNames[1].symbolNameList.lastOrNull()?.text ?: return "map"

        return "map<$key, $value>"
    }
}
