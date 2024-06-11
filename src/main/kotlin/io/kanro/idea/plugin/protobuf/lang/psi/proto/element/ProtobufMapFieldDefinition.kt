package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.string.toPascalCase
import javax.swing.Icon

interface ProtobufMapFieldDefinition : ProtobufFieldLike, ProtobufNumberScope, ProtobufMultiNameDefinition {
    fun entryName(): String? {
        return "${name()} Entry".toPascalCase()
    }

    override fun scope(): QualifiedName? {
        return owner()?.scope()?.append(entryName())
    }

    override fun items(): Array<ProtobufScopeItem> {
        return emptyArray()
    }

    override fun fieldValueType(): ValueType = ValueType.MESSAGE

    fun entryFields(): Array<LookupElementBuilder> {
        val typeNames = findChildren<ProtobufTypeName>()
        val key = typeNames.getOrNull(0)
        val value = typeNames.getOrNull(1)

        val keyTypeName = key?.leaf()?.text ?: "?"
        val valueTypeName = value?.leaf()?.text ?: "?"

        return arrayOf(
            LookupElementBuilder.create("key")
                .withIcon(ProtobufIcons.FIELD)
                .withTailText(": $keyTypeName = 1", true)
                .withTypeText("field")
                .withPsiElement(key),
            LookupElementBuilder.create("value")
                .withIcon(ProtobufIcons.FIELD)
                .withTailText(": $valueTypeName = 2", true)
                .withTypeText("field")
                .withPsiElement(value),
        )
    }

    override fun type(): String {
        return "field"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.FIELD
    }

    override fun fieldType(): String? {
        val typeNames = findChildren<ProtobufTypeName>()
        if (typeNames.size != 2) return "map"
        val key = typeNames[0].leaf().text ?: return "map"
        val value = typeNames[1].leaf().text ?: return "map"

        return "map<$key, $value>"
    }

    override fun names(): Set<String> {
        return setOfNotNull(name(), jsonName(), entryName())
    }
}
