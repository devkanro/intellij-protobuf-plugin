package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufNumberScope
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
        if (resourceType() != null) return ProtobufIcons.RESOURCE_MESSAGE
        return ProtobufIcons.MESSAGE
    }

    override fun tailText(): String? {
        return resourceType()?.let { ": $it" }
    }

    fun resourceType(): String? {
        if (this !is ProtobufOptionOwner) return null
        return CachedValuesManager.getCachedValue(this) {
            options(AipOptions.resourceOption).forEach {
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
}
