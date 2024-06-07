package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.ElementDescriptionProvider
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition

class ProtobufElementDescriptionProvider : ElementDescriptionProvider {
    override fun getElementDescription(
        element: PsiElement,
        location: ElementDescriptionLocation,
    ): String? {
        val definition = element as? ProtobufDefinition ?: return null
        return when (location) {
            is UsageViewLongNameLocation -> definition.qualifiedName()?.toString()
            is UsageViewShortNameLocation -> definition.name()
            is UsageViewTypeLocation -> definition.type()
            else -> null
        }
    }
}
