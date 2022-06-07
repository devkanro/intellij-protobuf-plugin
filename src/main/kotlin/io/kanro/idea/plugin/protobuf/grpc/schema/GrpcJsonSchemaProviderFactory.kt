package io.kanro.idea.plugin.protobuf.grpc.schema

import com.intellij.httpClient.http.request.psi.HttpMessageBody
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.json.psi.JsonFile
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.parentOfType
import com.jetbrains.jsonSchema.extension.ContentAwareJsonSchemaFileProvider
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceMethodIndex
import io.kanro.idea.plugin.protobuf.grpc.request.GrpcRequestExecutionSupport
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

@Suppress("UnstableApiUsage")
class GrpcJsonSchemaProviderFactory : ContentAwareJsonSchemaFileProvider {
    override fun getSchemaFile(psiFile: PsiFile): VirtualFile? {
        if (psiFile !is JsonFile) return null
        val host = InjectedLanguageManager.getInstance(psiFile.project).getInjectionHost(psiFile)
        if (host !is HttpMessageBody) return null
        val request = host.parentOfType<HttpRequest>() ?: return null
        if (request.method?.text !in GrpcRequestExecutionSupport.supportedMethod) return null
        val methodName = request.requestTarget?.pathAbsolute?.text?.trim('/') ?: return null
        val rpcDefinition = StubIndex.getElements(
            ServiceMethodIndex.key, methodName,
            psiFile.project, GlobalSearchScope.allScope(psiFile.project),
            ProtobufRpcDefinition::class.java
        ).firstOrNull() ?: return null
        val input = rpcDefinition.rpcIOList.firstOrNull()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
            ?: return null
        return null
    }
}