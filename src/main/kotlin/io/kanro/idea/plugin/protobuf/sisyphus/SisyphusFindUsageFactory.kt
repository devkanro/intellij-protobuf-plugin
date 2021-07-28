package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.find.findUsages.FindUsagesHandlerFactory
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike

class SisyphusFindUsageFactory : FindUsagesHandlerFactory() {
    override fun canFindUsages(element: PsiElement): Boolean {
        return element is ProtobufDefinition
    }

    override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler? {
        if (forHighlightUsages) return null
        if (element !is ProtobufDefinition) return null

        return when (element) {
            is ProtobufMessageDefinition -> {
                ProtoDefinitionFindUsage(element, listOfNotNull(element.toClass()).toTypedArray())
            }
            is ProtobufEnumDefinition -> {
                ProtoDefinitionFindUsage(element, listOfNotNull(element.toClass()).toTypedArray())
            }
            is ProtobufServiceDefinition -> {
                ProtoDefinitionFindUsage(element, listOfNotNull(element.toClass(), element.toClientClass()).toTypedArray())
            }
            is ProtobufEnumValueDefinition -> {
                ProtoDefinitionFindUsage(element, listOfNotNull(element.toEnumConstant()).toTypedArray())
            }
            is ProtobufRpcDefinition -> {
                ProtoDefinitionFindUsage(
                    element,
                    listOfNotNull(element.toMethod(), element.toClientMethod()).toTypedArray()
                )
            }
            is ProtobufFieldLike -> {
                ProtoDefinitionFindUsage(
                    element,
                    listOfNotNull(element.toGetter(), element.toSetter()).toTypedArray()
                )
            }
            else -> null
        }
    }

    private class ProtoDefinitionFindUsage(psiElement: PsiElement, private val secondaryElements: Array<PsiElement>) :
        FindUsagesHandler(psiElement) {
        override fun getSecondaryElements(): Array<PsiElement> {
            return secondaryElements
        }
    }
}
