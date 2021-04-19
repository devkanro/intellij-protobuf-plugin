package io.kanro.idea.plugin.protobuf.aip.reference.contributor

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.aip.reference.AipResourceReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufRpcInputFieldReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufRpcOutputFieldReference
import io.kanro.idea.plugin.protobuf.aip.reference.ProtobufTypeNameInStringReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.string.splitToRange
import io.kanro.idea.plugin.protobuf.string.trim

class AipReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(ProtobufStringValue::class.java)
                .inside(ProtobufFieldDefinition::class.java)
                .inside(ProtobufOptionAssign::class.java),
            AipResourceReferenceProvider()
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(ProtobufStringValue::class.java)
                .inside(ProtobufRpcDefinition::class.java)
                .inside(ProtobufOptionAssign::class.java),
            AipFieldReferenceProvider()
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(ProtobufStringValue::class.java)
                .inside(ProtobufRpcDefinition::class.java)
                .inside(ProtobufOptionAssign::class.java),
            AipTypeNameReferenceProvider()
        )
    }
}

class AipResourceReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val stringValue = element as? ProtobufStringValue ?: return arrayOf()
        val reference = getReference(stringValue) ?: return arrayOf()
        return arrayOf(reference)
    }

    private fun getReference(element: ProtobufStringValue): PsiReference? {
        val hover = element.parentOfType<ProtobufOptionHover>() ?: return null
        if (!hover.isOption(AipOptions.resourceReferenceOption)) return null
        if (hover.value(AipOptions.resourceTypeField)?.stringValue == element) return AipResourceReference(element)
        if (hover.value(AipOptions.resourceChildTypeField)?.stringValue == element) return AipResourceReference(element)
        return null
    }
}

class AipFieldReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val stringValue = element as? ProtobufStringValue ?: return arrayOf()
        val reference = getReference(stringValue) ?: return arrayOf()
        return arrayOf(reference)
    }

    private fun getReference(element: ProtobufStringValue): PsiReference? {
        if (element.textMatches("\"*\"")) return null
        val assign = element.parentOfType<ProtobufValueAssign>() ?: return null
        val targetField = assign.field()?.qualifiedName()
        if (targetField == AipOptions.httpRuleBodyName) return ProtobufRpcInputFieldReference(element)
        if (targetField == AipOptions.httpRuleResponseBodyName) return ProtobufRpcOutputFieldReference(element)
        return null
    }
}

class AipTypeNameReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element !is ProtobufStringValue) return arrayOf()
        val assign = element.parentOfType<ProtobufValueAssign>() ?: return arrayOf()
        val targetField = assign.field()?.qualifiedName()
        if (targetField == AipOptions.lroMetadataName) return getReference(element)
        if (targetField == AipOptions.lroResponseName) return getReference(element)
        return arrayOf()
    }

    private fun getReference(element: ProtobufStringValue): Array<PsiReference> {
        val text = element.text
        val ranges = text.splitToRange('.', true)
        var reference: ProtobufTypeNameInStringReference? = null
        return ranges.asReversed().map {
            ProtobufTypeNameInStringReference(
                element, it.trim(text, '"'), reference
            ).apply {
                reference = this
            }
        }.toTypedArray()
    }
}
