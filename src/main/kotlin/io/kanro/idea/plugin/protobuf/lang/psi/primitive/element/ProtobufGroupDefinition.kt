package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.openapi.util.text.StringUtil
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import io.kanro.idea.plugin.protobuf.string.toSnakeCase
import javax.swing.Icon

interface ProtobufGroupDefinition : ProtobufFieldLike, ProtobufNumberScope, ProtobufMultiNameDefinition {
    override fun type(): String {
        return "group"
    }
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.GROUP_FIELD
    }
    override fun fieldType(): String? {
        return identifier()?.text
    }
    override fun name(): String? {
        return identifier()?.text?.let { StringUtil.wordsToBeginFromLowerCase(it) }
    }
    override fun names(): Set<String> {
        val groupName = identifier()?.text ?: return emptySet()
        return setOf(groupName, groupName.toSnakeCase(), groupName.toCamelCase())
    }
}
