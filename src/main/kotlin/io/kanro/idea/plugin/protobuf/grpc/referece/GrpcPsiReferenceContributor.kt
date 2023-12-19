package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.grpc.isNativeGrpc
import io.kanro.idea.plugin.protobuf.grpc.isTranscoding

class GrpcPsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(HttpRequestTarget::class.java).with(
                object : PatternCondition<HttpRequestTarget>("GRPC METHOD") {
                    override fun accepts(
                        t: HttpRequestTarget,
                        context: ProcessingContext?,
                    ): Boolean {
                        return t.parentOfType<HttpRequest>()?.isNativeGrpc() ?: false
                    }
                },
            ),
            GrpcUrlReferenceProvider(),
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(HttpRequestTarget::class.java).with(
                object : PatternCondition<HttpRequestTarget>("GRPC TRANSCODING QUERY") {
                    override fun accepts(
                        t: HttpRequestTarget,
                        context: ProcessingContext?,
                    ): Boolean {
                        return t.parentOfType<HttpRequest>()?.isTranscoding() ?: false
                    }
                },
            ),
            GrpcTranscodingQueryReferenceProvider(),
        )
    }
}
