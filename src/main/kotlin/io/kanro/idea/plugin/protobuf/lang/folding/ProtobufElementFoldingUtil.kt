package io.kanro.idea.plugin.protobuf.lang.folding

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

object ProtobufElementFoldingUtil {
    fun createDefinitionFold(element: ProtobufElement): FoldingDescriptor? {
        val lBrace = element.node.findChildByType(ProtobufTokens.LBRACE)?.psi ?: return null
        val rBrace = element.lastChild.takeIf { it.elementType == ProtobufTokens.RBRACE } ?: return null
        return FoldingDescriptor(element, lBrace.startOffset, rBrace.endOffset, null, "{...}")
    }
}
