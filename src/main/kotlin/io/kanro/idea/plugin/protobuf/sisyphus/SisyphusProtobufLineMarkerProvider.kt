package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.search.searches.OverridingMethodsSearch
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class SisyphusProtobufLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val identifier = element as? ProtobufIdentifier ?: return

        when (val owner = identifier.parent) {
            is ProtobufRpcDefinition -> {
                val serviceBase = owner.owner()?.externalQualifiedName(SisyphusIndexProvider.key) ?: return
                val clazz = JavaPsiFacade.getInstance(element.project)
                    .findClass(serviceBase.toString(), FileResolver.searchScope(element)) as PsiClass
                val method =
                    clazz.findMethodsByName(owner.externalName(SisyphusIndexProvider.key), true).firstOrNull() ?: return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTED_RPC)
                        .setTargets(OverridingMethodsSearch.search(method).toList())
                        .setTooltipText("Implemented")
                result.add(builder.createLineMarkerInfo(element))
            }
            is ProtobufServiceDefinition -> {
                val serviceBase = owner.externalQualifiedName(SisyphusIndexProvider.key) ?: return
                val clazz = JavaPsiFacade.getInstance(element.project)
                    .findClass(serviceBase.toString(), FileResolver.searchScope(element)) as PsiClass
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
