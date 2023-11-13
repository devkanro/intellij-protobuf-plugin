package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpMessageBody
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.json.psi.JsonElement
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.grpc.request.GrpcRequestExecutionSupport

object GrpcJsonBody : PatternCondition<JsonElement>("GRPC JSON BODY") {
    override fun accepts(
        t: JsonElement,
        context: ProcessingContext?,
    ): Boolean {
        val host =
            InjectedLanguageManager.getInstance(t.project).getInjectionHost(t) as? HttpMessageBody
                ?: return false
        val request = host.parentOfType<HttpRequest>() ?: return false
        return request.method?.text in GrpcRequestExecutionSupport.supportedMethod
    }
}

class GrpcJsonReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(JsonStringLiteral::class.java).withParent(JsonProperty::class.java)
                .isFirstAcceptedChild(PlatformPatterns.not(PlatformPatterns.psiElement().whitespace()))
                .with(GrpcJsonBody),
            GrpcMessageFieldReferenceProvider(),
        )
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(JsonStringLiteral::class.java).withParent(JsonProperty::class.java)
                .afterLeaf(":")
                .with(GrpcJsonBody),
            GrpcStringLiteralValueReferenceProvider(),
        )
    }
}
