package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufMultiNameDefinition
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

    override fun fieldType(): String? {
        resourceType()?.let {
            return it
        }
        return findChild<ProtobufTypeName>()?.symbolNameList?.lastOrNull()?.text
    }

    override fun names(): Set<String> {
        return setOfNotNull(name(), jsonName())
    }
}
