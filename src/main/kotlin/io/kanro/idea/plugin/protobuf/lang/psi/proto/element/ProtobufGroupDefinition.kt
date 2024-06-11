package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import io.kanro.idea.plugin.protobuf.string.toSnakeCase
import javax.swing.Icon

interface ProtobufGroupDefinition : ProtobufFieldLike, ProtobufNumberScope, ProtobufMultiNameDefinition {
    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun type(): String {
        return "group"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.GROUP_FIELD
    }

    override fun fieldType(): String? {
        return identifier()?.text
    }

    override fun fieldValueType(): ValueType = ValueType.MESSAGE

    override fun name(): String? {
        return identifier()?.text
    }

    override fun names(): Set<String> {
        val groupName = identifier()?.text ?: return emptySet()
        return setOf(groupName, groupName.toSnakeCase(), groupName.toCamelCase(), groupName.lowercase())
    }
}
