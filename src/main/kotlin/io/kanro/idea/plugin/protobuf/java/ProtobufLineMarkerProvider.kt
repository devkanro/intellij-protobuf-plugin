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
                val methods = owner.toImplBaseMethod()?.let {
                    OverridingMethodsSearch.search(it).toList()
                } ?: listOf()
                val ktMethods = owner.toCoroutineImplBaseMethod()?.let {
                    OverridingMethodsSearch.search(it).toList()
                } ?: listOf()
                if (methods.isEmpty() && ktMethods.isEmpty()) return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTED_RPC)
                        .setTargets(methods + ktMethods)
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element))
            }
            is ProtobufServiceDefinition -> {
                val apis = owner.toImplBaseClass()?.let {
                    DirectClassInheritorsSearch.search(it).findAll().toList()
                } ?: listOf()
                val ktApis = owner.toCoroutineImplBaseClass()?.let {
                    DirectClassInheritorsSearch.search(it).findAll().toList()
                } ?: listOf()
                if (apis.isEmpty() && ktApis.isEmpty()) return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTED_SERVICE)
                        .setTargets(apis + ktApis)
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element))
            }
        }
    }
}
