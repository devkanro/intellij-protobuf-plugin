package io.kanro.idea.plugin.protobuf.java

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.search.searches.OverridingMethodsSearch
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class ProtobufLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val identifier = element as? ProtobufIdentifier ?: return
        if (!isJava(element)) return

        when (val owner = identifier.parent) {
            is ProtobufRpcDefinition -> {
                val method = owner.toImplBaseMethod() ?: return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTED_RPC)
                        .setTargets(OverridingMethodsSearch.search(method).toList())
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element))
            }
            is ProtobufServiceDefinition -> {
                val clazz = owner.toImplBaseClass() ?: return
                val apis = DirectClassInheritorsSearch.search(clazz).findAll().toList()
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTED_SERVICE)
                        .setTargets(apis)
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element))
            }
        }
    }
}
