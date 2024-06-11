package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.token.ProtobufKeywordToken
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren

fun ProtobufRpcIO.stream(): Boolean {
    this.walkChildren<PsiElement>(false) {
        if (it.elementType is ProtobufKeywordToken && it.textMatches("stream")) {
            return true
        }
    }
    return false
}
