package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.stringValue
import io.kanro.idea.plugin.protobuf.lang.psi.value
import javax.swing.Icon

interface ProtobufMessageDefinition : ProtobufNumberScope, ProtobufDefinition {
    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun type(): String {
        if (resourceType() != null) return "resource"
        return "message"
    }

    override fun getIcon(unused: Boolean): Icon? {
        if (resourceType() != null) return Icons.RESOURCE_MESSAGE
        return Icons.MESSAGE
    }

    override fun tailText(): String? {
        return resourceType()?.let { ": $it" }
    }

    fun resourceType(): String? {
        if (this !is ProtobufOptionOwner) return null
        return CachedValuesManager.getCachedValue(this) {
            options(AipOptions.resourceOption).forEach {
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
}
