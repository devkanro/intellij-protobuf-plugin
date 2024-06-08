package io.kanro.idea.plugin.protobuf.grpc.gutter

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.httpClient.actions.generation.HttpRequestUrlPathInfo
import com.intellij.httpClient.actions.generation.HttpRequestUrlsGenerationRequest
import com.intellij.httpClient.actions.generation.RequestUrlContextInfo
import com.intellij.httpClient.executor.util.unwrap
import com.intellij.httpClient.http.request.microservices.OpenInHttpClientLineMarkerBuilder
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.firstLeaf
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stream
import javax.swing.Icon

class GrpcRunRequestGutterProvider : RelatedItemLineMarkerProvider() {
    override fun getIcon(): Icon {
        return ProtobufIcons.PROCEDURE
    }

    override fun getId(): String {
        return "GrpcRunRequestGutterProvider"
    }

    override fun getName(): String {
        return "Run gRpc"
    }

    @Suppress("UnstableApiUsage")
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
    ) {
        if (element !is ProtobufRpcDefinition) return
        if (element.input() == element.output()) return
        if (element.input()?.stream() != false) return
        val service = element.owner() ?: return
        val serviceName = service.qualifiedName() ?: return
        val methodName = element.name() ?: return

        val request =
            HttpRequestUrlsGenerationRequest(
                listOfNotNull(
                    HttpRequestUrlPathInfo.create(
                        element.project,
                        "$serviceName/$methodName",
                        listOf("GRPC"),
                    ).unwrap(false),
                ),
                RequestUrlContextInfo.createNonHttp(
                    element.project,
                    listOf("http://", "https://"),
                    listOf("localhost:9090"),
                ).unwrap(false) ?: return,
            )

        result +=
            OpenInHttpClientLineMarkerBuilder.fromGenerationRequest(element.project, request)
                .createLineMarkerInfo(element.firstLeaf(), ProtobufIcons.PROCEDURE)
    }
}
