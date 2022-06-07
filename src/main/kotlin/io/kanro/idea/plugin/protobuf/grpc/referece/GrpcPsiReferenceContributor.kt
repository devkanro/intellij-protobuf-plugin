package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpMethod
import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.grpc.request.GrpcRequestExecutionSupport
import io.kanro.idea.plugin.protobuf.lang.psi.prev

class GrpcPsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(HttpRequestTarget::class.java)
                .with(object : PatternCondition<HttpRequestTarget>("GRPC METHOD") {
                    override fun accepts(t: HttpRequestTarget, context: ProcessingContext?): Boolean {
                        return t.prev<HttpMethod>()?.text in GrpcRequestExecutionSupport.supportedMethod
                    }
                }),
            GrpcUrlReferenceProvider()
        )
    }
}

