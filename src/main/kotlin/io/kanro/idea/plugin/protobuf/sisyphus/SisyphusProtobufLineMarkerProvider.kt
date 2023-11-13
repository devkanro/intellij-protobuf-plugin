package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.search.searches.OverridingMethodsSearch
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class SisyphusProtobufLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
    ) {
        val identifier = element as? ProtobufIdentifier ?: return
        if (!isSisyphus(element)) return

        when (val owner = identifier.parent) {
            is ProtobufRpcDefinition -> {
                val method = owner.toMethod() ?: return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(ProtobufIcons.IMPLEMENTED_RPC)
                        .setTargets(OverridingMethodsSearch.search(method).toList())
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element.identifierLiteral ?: element))
            }
            is ProtobufServiceDefinition -> {
                val clazz = owner.toClass() ?: return
                val apis = DirectClassInheritorsSearch.search(clazz).findAll().toList()
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(ProtobufIcons.IMPLEMENTED_SERVICE)
                        .setTargets(apis)
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element.identifierLiteral ?: element))
            }
        }
    }
}
