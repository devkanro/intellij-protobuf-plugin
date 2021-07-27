package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufMultiNameDefinition
import javax.swing.Icon

interface ProtobufMapFieldDefinition : ProtobufFieldLike, ProtobufMultiNameDefinition {
    override fun type(): String {
        return "field"
    }
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }
    override fun fieldType(): String? {
        val typeNames = findChildren<ProtobufTypeName>()
        if (typeNames.size != 2) return "map"
        val key = typeNames[0].symbolNameList.lastOrNull()?.text ?: return "map"
        val value = typeNames[1].symbolNameList.lastOrNull()?.text ?: return "map"

        return "map<$key, $value>"
    }
    override fun names(): Set<String> {
        return setOfNotNull(name(), jsonName())
    }
}
