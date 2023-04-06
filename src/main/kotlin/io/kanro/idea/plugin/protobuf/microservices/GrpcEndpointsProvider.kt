package io.kanro.idea.plugin.protobuf.microservices

import com.intellij.microservices.endpoints.API_DEFINITION_TYPE
import com.intellij.microservices.endpoints.EndpointType
import com.intellij.microservices.endpoints.EndpointsFilter
import com.intellij.microservices.endpoints.EndpointsProvider
import com.intellij.microservices.endpoints.EndpointsUrlTargetProvider
import com.intellij.microservices.endpoints.FrameworkPresentation
import com.intellij.microservices.endpoints.SearchScopeEndpointsFilter
import com.intellij.microservices.url.UrlTargetInfo
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.microservices.model.ProtobufRpcModel
import io.kanro.idea.plugin.protobuf.microservices.model.ProtobufServiceModel

@Suppress("UnstableApiUsage")
class GrpcEndpointsProvider : EndpointsUrlTargetProvider<ProtobufServiceModel, ProtobufRpcModel> {
    override val endpointType: EndpointType = API_DEFINITION_TYPE
    override val presentation: FrameworkPresentation =
        FrameworkPresentation("gRPC", "gRPC Specification", ProtobufIcons.PROCEDURE)

    override fun getDocumentationElement(group: ProtobufServiceModel, endpoint: ProtobufRpcModel): PsiElement? {
        return endpoint.getPsi()
    }

    override fun getUrlTargetInfo(group: ProtobufServiceModel, endpoint: ProtobufRpcModel): Iterable<UrlTargetInfo> {
        return listOfNotNull(endpoint.getPsi()?.let { GrpcUrlTargetInfo(it) })
    }

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<ProtobufServiceModel> {
        return when (filter) {
            is SearchScopeEndpointsFilter -> {
                val scope = filter.searchScope
                FileTypeIndex.getFiles(ProtobufFileType.INSTANCE, scope).flatMap {
                    val psi = PsiManager.getInstance(project).findFile(it) as? ProtobufFile
                        ?: return@flatMap listOf<ProtobufServiceModel>()
                    psi.services().map {
                        ProtobufServiceModel(SmartPointerManager.createPointer(it))
                    }
                }
            }

            else -> listOf()
        }
    }

    override fun getEndpointPresentation(group: ProtobufServiceModel, endpoint: ProtobufRpcModel): ItemPresentation {
        return endpoint.getPsi()!!
    }

    override fun getEndpoints(group: ProtobufServiceModel): Iterable<ProtobufRpcModel> {
        return group.getMethods()
    }

    override fun getModificationTracker(project: Project): ModificationTracker {
        return PsiManager.getInstance(project).modificationTracker.forLanguage(ProtobufLanguage)
    }

    override fun getStatus(project: Project): EndpointsProvider.Status {
        return if (FileTypeIndex.getFiles(ProtobufFileType.INSTANCE, GlobalSearchScope.projectScope(project))
                .isEmpty()
        ) {
            EndpointsProvider.Status.AVAILABLE
        } else {
            EndpointsProvider.Status.HAS_ENDPOINTS
        }
    }

    override fun isValidEndpoint(group: ProtobufServiceModel, endpoint: ProtobufRpcModel): Boolean {
        return endpoint.getPsi()?.isValid == true
    }
}
