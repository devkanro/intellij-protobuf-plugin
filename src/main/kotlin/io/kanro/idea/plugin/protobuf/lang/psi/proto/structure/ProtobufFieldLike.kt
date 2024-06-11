package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.string.toCamelCase

interface ProtobufFieldLike : ProtobufDefinition, ProtobufNumbered {
    fun fieldName(): String? {
        return name()
    }

    fun fieldType(): String?

    fun fieldValueType(): ValueType

    fun jsonName(): String? {
        return CachedValuesManager.getCachedValue(this) {
            val option = (this as? ProtobufOptionOwner)?.options("json_name")?.lastOrNull()
            val result =
                option?.value()?.toString()
                    ?: fieldName()?.toCamelCase()
            CachedValueProvider.Result.create(
                result,
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
    }

    override fun tailText(): String? {
        return ": ${fieldType()} = ${number()}"
    }
}
