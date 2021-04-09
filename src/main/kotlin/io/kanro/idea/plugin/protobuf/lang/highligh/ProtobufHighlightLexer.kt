package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.lexer.DelegateLexer
import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

class ProtobufHighlightLexer(delegate: Lexer) : DelegateLexer(delegate) {
    override fun getTokenType(): IElementType? {
        val type = super.getTokenType()
        if (type == ProtobufTokens.IDENTIFIER_LITERAL) {
            if (BuiltInType.isBuiltInType(tokenText)) {
                return ProtobufTokens.BUILT_IN_TYPE
            }
        }
        return type
    }
}
