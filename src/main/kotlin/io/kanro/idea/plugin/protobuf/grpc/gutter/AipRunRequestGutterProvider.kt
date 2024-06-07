package io.kanro.idea.plugin.protobuf.grpc.gutter

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.template.impl.TemplateImpl
import com.intellij.httpClient.actions.generation.HttpRequestUrlPathInfo
import com.intellij.httpClient.actions.generation.RequestBody
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import javax.swing.Icon

class AipRunRequestGutterProvider : RelatedItemLineMarkerProvider() {
    override fun getIcon(): Icon {
        return ProtobufIcons.PROCEDURE
    }

    override fun getId(): String {
        return "AipRunRequestGutterProvider"
    }

    override fun getName(): String {
        return "Run gRpc via HTTP transcoding"
    }

    @Suppress("UnstableApiUsage")
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
    ) {
//        if (element !is ProtobufTextField) return
//        val option = element.parentOfType<ProtobufRpcOption>() ?: return
//        val grpcDefinition = element.parentOfType<ProtobufRpcDefinition>() ?: return
//        if (!option.isOption(AipOptions.httpOption)) return
//        val fieldName = element.field()?.qualifiedName() ?: return
//        if (fieldName !in AipOptions.httpRulesName) return
//        val httpMethod = fieldName.lastComponent?.uppercase() ?: return
//        val path = element.value()?.toString() ?: return
//
//        val service = grpcDefinition.owner() ?: return
//        val serviceName = service.qualifiedName() ?: return
//        val methodName = grpcDefinition.name() ?: return
//        val hasBody = httpMethod in listOf("POST", "PUT", "PATCH") && option.value(AipOptions.httpRuleBodyField) != null
//
//        val request =
//            HttpRequestUrlsGenerationRequest(
//                listOfNotNull(
//                    HttpRequestUrlPathInfo.create(
//                        element.project,
//                        PathTemplate.create(path).withoutVars().toString(),
//                        listOf(httpMethod),
//                        {
//                            computeCustomRequestBodyTemplate(it, serviceName.toString(), methodName, hasBody)
//                        },
//                    ).unwrap(false),
//                ),
//                RequestUrlContextInfo.create(
//                    element.project,
//                    listOf("http://", "https://"),
//                    listOf("localhost:8080"),
//                ).unwrap(false) ?: return,
//            )
//
//        result +=
//            OpenInHttpClientLineMarkerBuilder.fromGenerationRequest(element.project, request)
//                .createLineMarkerInfo(element.firstLeaf(), ProtobufIcons.PROCEDURE_HTTP)
    }

    private fun computeCustomRequestBodyTemplate(
        info: HttpRequestUrlPathInfo,
        serviceName: String,
        methodName: String,
        hasBody: Boolean,
    ): HttpRequestUrlPathInfo.Computed {
        val raw = info.compute()
        return if (hasBody) {
            HttpRequestUrlPathInfo.Computed(
                raw.baseInfo,
                requestBody =
                    RequestBody.CustomRequestBodyTemplate(
                        TemplateImpl(
                            "transcodingWithBody",
                            "\ngrpc-method: $serviceName/$methodName\ncontent-type: application/json\n\n{\n}",
                            "grpc",
                        ),
                    ),
            )
        } else {
            HttpRequestUrlPathInfo.Computed(
                raw.baseInfo,
                requestBody =
                    RequestBody.CustomRequestBodyTemplate(
                        TemplateImpl(
                            "transcodingWithBody",
                            "\ngrpc-method: $serviceName/$methodName",
                            "grpc",
                        ),
                    ),
            )
        }
    }
}
