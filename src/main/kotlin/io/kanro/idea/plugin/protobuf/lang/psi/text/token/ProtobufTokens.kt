package io.kanro.idea.plugin.protobuf.lang.psi.text.token

import com.intellij.psi.tree.IElementType

object ProtoTextTokens {
    private val types: MutableMap<String, IElementType> = mutableMapOf()

    private fun put(element: IElementType): IElementType {
        return put(element.toString(), element)
    }

    private fun put(
        name: String,
        element: IElementType,
    ): IElementType {
        return types.getOrPut(name) {
            element
        }
    }

    fun get(token: String): IElementType {
        return types[token] ?: throw AssertionError("Unknown token type: $token")
    }

    @JvmField
    val ASSIGN = put(ProtoTextToken("="))

    @JvmField
    val COLON = put(ProtoTextToken(":"))

    @JvmField
    val COMMA = put(ProtoTextToken(","))

    @JvmField
    val DOT = put(ProtoTextToken("."))

    @JvmField
    val GT = put(ProtoTextToken(">"))

    @JvmField
    val LBRACE = put(ProtoTextToken("{"))

    @JvmField
    val LBRACK = put(ProtoTextToken("["))

    @JvmField
    val LPAREN = put(ProtoTextToken("("))

    @JvmField
    val LT = put(ProtoTextToken("<"))

    @JvmField
    val MINUS = put(ProtoTextToken("-"))

    @JvmField
    val PLUS = put(ProtoTextToken("+"))

    @JvmField
    val RBRACE = put(ProtoTextToken("}"))

    @JvmField
    val RBRACK = put(ProtoTextToken("]"))

    @JvmField
    val RPAREN = put(ProtoTextToken(")"))

    @JvmField
    val SEMI = put(ProtoTextToken(";"))

    @JvmField
    val SLASH = put(ProtoTextToken("/"))

    @JvmField
    val FLOAT_LITERAL = put("FLOAT_LITERAL", ProtoTextToken("float"))

    @JvmField
    val IDENTIFIER_LITERAL = put("IDENTIFIER_LITERAL", ProtoTextToken("identifier"))

    @JvmField
    val INTEGER_LITERAL = put("INTEGER_LITERAL", ProtoTextToken("integer"))

    @JvmField
    val STRING_LITERAL = put("STRING_LITERAL", ProtoTextToken("string"))

    @JvmField
    val BUILT_IN_TYPE = put("BUILT_IN_TYPE", ProtoTextToken("BUILT_IN_TYPE"))

    @JvmField
    val IDENTIFIER_AFTER_NUMBER = put(ProtoTextToken("IDENTIFIER_AFTER_NUMBER"))

    @JvmField
    val SHARP_LINE_COMMENT = put(ProtoTextCommentToken("SHARP_LINE_COMMENT"))

    @JvmField
    val SYMBOL = put(ProtoTextToken("SYMBOL"))

    @JvmField
    val FALSE = put(ProtoTextKeywordToken("false"))

    @JvmField
    val TRUE = put(ProtoTextKeywordToken("true"))
}
