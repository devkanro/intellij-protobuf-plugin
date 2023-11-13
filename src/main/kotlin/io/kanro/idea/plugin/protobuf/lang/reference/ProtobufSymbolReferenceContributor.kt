package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHost
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

class ProtobufSymbolReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(ProtobufSymbolReferenceHost::class.java),
            ProtobufSymbolReferenceProvider,
        )
    }
}

object ProtobufSymbolReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext,
    ): Array<PsiReference> {
        element as ProtobufSymbolReferenceHost
        return CachedValuesManager.getCachedValue(element) {
            val hover =
                element.referencesHover()
                    ?: return@getCachedValue CachedValueProvider.Result.create(
                        PsiReference.EMPTY_ARRAY,
                        PsiModificationTracker.MODIFICATION_COUNT,
                    )
            val symbol = hover.symbol()
            if (!hover.absolutely() && symbol.componentCount == 1 && BuiltInType.isBuiltInType(symbol.firstComponent!!)) {
                return@getCachedValue CachedValueProvider.Result.create(
                    PsiReference.EMPTY_ARRAY,
                    PsiModificationTracker.MODIFICATION_COUNT,
                )
            }
            var reference: ProtobufTypeNameReference? = null
            val result =
                symbol.components.reversed().mapIndexed { index, name ->
                    ProtobufTypeNameReference(element, hover, symbol.componentCount - 1 - index, reference).apply {
                        reference = this
                    }
                }.toTypedArray<PsiReference>()
            CachedValueProvider.Result.create(result, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }
}
