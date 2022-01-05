package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stringValue
import io.kanro.idea.plugin.protobuf.lang.psi.value
import javax.swing.Icon

interface ProtobufFieldDefinition : ProtobufFieldLike, ProtobufMultiNameDefinition {
    override fun type(): String {
        return "field"
    }
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }
    fun resourceType(): String? {
        if (this !is ProtobufOptionOwner) return null
        return CachedValuesManager.getCachedValue(this) {
            options(AipOptions.resourceReferenceOption).forEach {
                it.value(AipOptions.resourceTypeField)?.stringValue()?.let {
                    return@getCachedValue CachedValueProvider.Result.create(
                        it,
                        PsiModificationTracker.MODIFICATION_COUNT
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
