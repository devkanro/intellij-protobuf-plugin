package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.httpClient.http.request.psi.HttpRequestTarget
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.aip.resolveInput
import io.kanro.idea.plugin.protobuf.aip.transcodingBody
import io.kanro.idea.plugin.protobuf.grpc.resolveRpc
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.filterItem
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike

class GrpcTranscodingQueryReference(private val name: String, element: HttpRequestTarget, range: TextRange) :
    PsiReferenceBase<HttpRequestTarget>(element, range), GrpcReference {
    override fun resolve(): PsiElement? {
        return element.query?.queryParameterList?.firstOrNull {
            it.queryParameterKey.text == name
        }?.queryParameterKey?.resolve()
    }

    override fun getVariants(): Array<Any> {
        val request = element.parentOfType<HttpRequest>() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val rpc = request.resolveRpc() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val input = rpc.resolveInput() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val body = rpc.transcodingBody()

        val existsFields = mutableSetOf<String>()

        when (body) {
            null -> {}
            "*" -> return ArrayUtilRt.EMPTY_OBJECT_ARRAY
            else -> existsFields += body
        }

        request.requestTarget?.query?.queryParameterList?.mapNotNull {
            it.queryParameterKey.name
        }?.let {
            existsFields += it
        }

        return input.filterItem<ProtobufFieldLike> { true }.mapNotNull { lookupFor(it, existsFields) }.toTypedArray()
    }

    private fun lookupFor(
        element: ProtobufFieldLike,
        existsFields: Set<String>,
    ): LookupElementBuilder? {
        val jsonName = element.jsonName() ?: return null
        if (jsonName in existsFields) return null
        if (element.name() in existsFields) return null

        return element.lookup(jsonName)?.withPresentableText(jsonName)?.let {
            if (element.name() != jsonName) {
                it.appendTailText(" (${element.name()})", true)
            } else {
                it
            }
        }?.withInsertHandler(SmartInsertHandler("="))
    }
}
