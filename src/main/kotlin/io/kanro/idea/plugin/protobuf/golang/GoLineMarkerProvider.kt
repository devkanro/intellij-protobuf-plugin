package io.kanro.idea.plugin.protobuf.golang

import com.goide.GoTypes
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoSpecType
import com.goide.psi.GoStructType
import com.goide.psi.GoTypeSpec
import com.goide.psi.impl.GoPsiImplUtil
import com.goide.psi.impl.GoPsiUtil
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

class GoLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element.elementType != GoTypes.IDENTIFIER) return
        when (val parent = element.parent) {
            is GoMethodDeclaration -> {
                val type = parent.resolveTypeSpec() ?: return
                val shadowedMethod =
                    type.allMethods.firstOrNull {
                        it != parent && GoPsiUtil.isSameNamedMethod(
                            parent,
                            it,
                            true
                        )
                    } as? GoMethodDeclaration ?: return
                val unimplementedServerName = shadowedMethod.receiverType?.text ?: return
                val indexName = "$unimplementedServerName.${parent.name}"
                val methods = StubIndex.getElements(
                    GoUnimplementedServerNameIndex.key,
                    indexName,
                    parent.project,
                    GlobalSearchScope.allScope(parent.project),
                    ProtobufElement::class.java
                )
                if (methods.isEmpty()) return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTING_RPC)
                        .setTargets(methods)
                        .setTooltipText("Implementing")
                result.add(builder.createLineMarkerInfo(element))
            }
            is GoSpecType -> {
                val spec = parent.parent as? GoTypeSpec ?: return
                val type = spec.getGoUnderlyingType(null) as? GoStructType ?: return
                val servers = mutableListOf<ProtobufElement>()
                GoPsiImplUtil.getAnonymousFieldDefinitions(type).forEach {
                    val anonymousFieldType = it.typeReferenceExpression?.resolveType(null) as? GoSpecType ?: return@forEach
                    servers += StubIndex.getElements(
                        GoUnimplementedServerNameIndex.key,
                        anonymousFieldType.identifier.text,
                        parent.project,
                        GlobalSearchScope.allScope(parent.project),
                        ProtobufElement::class.java
                    )
                }
                if (servers.isEmpty()) return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(Icons.IMPLEMENTING_SERVICE)
                        .setTargets(servers)
                        .setTooltipText("Implementing")
                result.add(builder.createLineMarkerInfo(element))
            }
            else -> return
        }
    }
}
