package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.support.Resources
import javax.swing.Icon

interface ProtobufFieldDefinition : ProtobufFieldLike {
    @JvmDefault
    override fun type(): String {
        return "field"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }

    @JvmDefault
    fun resourceName(): String? {
        if (this !is ProtobufOptionOwner) return null
        return CachedValuesManager.getCachedValue(this) {
            options(Resources.resourceReferenceOption).forEach {
                it.value(Resources.resourceTypeField)?.stringValue?.value()?.let {
                    return@getCachedValue CachedValueProvider.Result.create(
                        it,
                        PsiModificationTracker.MODIFICATION_COUNT
                    )
                }
            }
            return@getCachedValue CachedValueProvider.Result.create(null, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }

    @JvmDefault
    override fun fieldType(): String? {
        resourceName()?.let {
            return it
        }
        return findChild<ProtobufTypeName>()?.symbolNameList?.lastOrNull()?.text
    }
}
