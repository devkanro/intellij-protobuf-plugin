package io.kanro.idea.plugin.protobuf.microservices

import com.intellij.microservices.url.Authority
import com.intellij.microservices.url.UrlPath
import com.intellij.microservices.url.UrlTargetInfo
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import javax.swing.Icon

@Suppress("UnstableApiUsage")
class GrpcUrlTargetInfo(private val rpc: ProtobufRpcDefinition) : UrlTargetInfo {
    override val authorities: List<Authority>
        get() = listOf(Authority.Placeholder())
    override val path: UrlPath
        get() = UrlPath.fromExactString("${rpc.owner()?.qualifiedName()}/${rpc.name()}")
    override val schemes: List<String>
        get() = listOf("http://", "https://")
    override val icon: Icon
        get() = Icons.PROCEDURE
    override val methods: Set<String>
        get() = setOf("GRPC")
    override val documentationPsiElement: PsiElement?
        get() = rpc

    override fun resolveToPsiElement(): PsiElement? {
        return rpc
    }
}