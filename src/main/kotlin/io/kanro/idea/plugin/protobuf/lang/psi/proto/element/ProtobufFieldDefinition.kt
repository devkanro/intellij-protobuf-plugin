package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType
import javax.swing.Icon

interface ProtobufFieldDefinition : ProtobufFieldLike, ProtobufMultiNameDefinition {
    override fun type(): String {
        return "field"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.FIELD
    }

    fun resourceType(): String? {
        if (this !is ProtobufOptionOwner) return null
        return CachedValuesManager.getCachedValue(this) {
            options(AipOptions.resourceReferenceOption).forEach {
                it.value(AipOptions.resourceTypeField)?.toString()?.let {
                    return@getCachedValue CachedValueProvider.Result.create(
                        it,
                        PsiModificationTracker.MODIFICATION_COUNT,
                    )
                }
            }
            return@getCachedValue CachedValueProvider.Result.create(null, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }

    override fun fieldValueType(): ValueType {
        val typeName = findChild<ProtobufTypeName>() ?: return ValueType.UNKNOWN

        when(typeName.text) {
            BuiltInType.BOOL.value() -> return ValueType.BOOLEAN
            BuiltInType.STRING.value(),
            BuiltInType.BYTES.value() -> return ValueType.STRING
            BuiltInType.INT32.value(),
            BuiltInType.INT64.value(),
            BuiltInType.UINT32.value(),
            BuiltInType.UINT64.value(),
            BuiltInType.FIXED32.value(),
            BuiltInType.FIXED64.value(),
            BuiltInType.SFIXED32.value(),
            BuiltInType.SFIXED64.value(),
            BuiltInType.SINT32.value(),
            BuiltInType.SINT64.value(),
            BuiltInType.FLOAT.value(),
            BuiltInType.DOUBLE.value() -> return ValueType.NUMBER
        }

        return when(typeName.resolve()) {
            is ProtobufMessageDefinition -> ValueType.MESSAGE
            is ProtobufEnumDefinition -> ValueType.ENUM
            else -> ValueType.UNKNOWN
        }
    }

    override fun fieldType(): String? {
        resourceType()?.let {
            return it
        }
        return findChild<ProtobufTypeName>()?.leaf()?.text
    }

    override fun names(): Set<String> {
        return setOfNotNull(name(), jsonName())
    }
}
