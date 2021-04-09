package io.kanro.idea.plugin.protobuf.lang.psi.token

import com.intellij.psi.tree.IElementType

object ProtobufTokens {
    private val types: MutableMap<String, IElementType> = mutableMapOf()

    private fun put(element: IElementType): IElementType {
        return put(element.toString(), element)
    }

    private fun put(name: String, element: IElementType): IElementType {
        return types.getOrPut(name) {
            element
        }
    }

    fun get(token: String): IElementType {
        return types[token] ?: throw AssertionError("Unknown token type: $token")
    }

    @JvmField
    val ASSIGN = put(ProtobufToken("="))
    @JvmField
    val COLON = put(ProtobufToken(":"))
    @JvmField
    val COMMA = put(ProtobufToken(","))
    @JvmField
    val DOT = put(ProtobufToken("."))
    @JvmField
    val GT = put(ProtobufToken(">"))
    @JvmField
    val LBRACE = put(ProtobufToken("{"))
    @JvmField
    val LBRACK = put(ProtobufToken("["))
    @JvmField
    val LPAREN = put(ProtobufToken("("))
    @JvmField
    val LT = put(ProtobufToken("<"))
    @JvmField
    val MINUS = put(ProtobufToken("-"))
    @JvmField
    val RBRACE = put(ProtobufToken("}"))
    @JvmField
    val RBRACK = put(ProtobufToken("]"))
    @JvmField
    val RPAREN = put(ProtobufToken(")"))
    @JvmField
    val SEMI = put(ProtobufToken(";"))
    @JvmField
    val SLASH = put(ProtobufToken("/"))

    @JvmField
    val FLOAT_LITERAL = put("FLOAT_LITERAL", ProtobufToken("float"))
    @JvmField
    val IDENTIFIER_LITERAL = put("IDENTIFIER_LITERAL", ProtobufToken("identifier"))
    @JvmField
    val INTEGER_LITERAL = put("INTEGER_LITERAL", ProtobufToken("integer"))
    @JvmField
    val STRING_LITERAL = put("STRING_LITERAL", ProtobufToken("string"))
    @JvmField
    val BUILT_IN_TYPE = put("BUILT_IN_TYPE", ProtobufToken("BUILT_IN_TYPE"))

    @JvmField
    val BLOCK_COMMENT = put(ProtobufCommentToken("BLOCK_COMMENT"))
    @JvmField
    val IDENTIFIER_AFTER_NUMBER = put(ProtobufToken("IDENTIFIER_AFTER_NUMBER"))
    @JvmField
    val LINE_COMMENT = put(ProtobufCommentToken("LINE_COMMENT"))
    @JvmField
    val SYMBOL = put(ProtobufToken("SYMBOL"))

    @JvmField
    val DEFAULT = put(ProtobufKeywordToken("default"))
    @JvmField
    val ENUM = put(ProtobufKeywordToken("enum"))
    @JvmField
    val EXTEND = put(ProtobufKeywordToken("extend"))
    @JvmField
    val EXTENSIONS = put(ProtobufKeywordToken("extensions"))
    @JvmField
    val FALSE = put(ProtobufKeywordToken("false"))
    @JvmField
    val GROUP = put(ProtobufKeywordToken("group"))
    @JvmField
    val IMPORT = put(ProtobufKeywordToken("import"))
    @JvmField
    val JSON_NAME = put(ProtobufKeywordToken("json_name"))
    @JvmField
    val MAP = put(ProtobufKeywordToken("map"))
    @JvmField
    val MAX = put(ProtobufKeywordToken("max"))
    @JvmField
    val MESSAGE = put(ProtobufKeywordToken("message"))
    @JvmField
    val ONEOF = put(ProtobufKeywordToken("oneof"))
    @JvmField
    val OPTION = put(ProtobufKeywordToken("option"))
    @JvmField
    val OPTIONAL = put(ProtobufKeywordToken("optional"))
    @JvmField
    val PACKAGE = put(ProtobufKeywordToken("package"))
    @JvmField
    val PUBLIC = put(ProtobufKeywordToken("public"))
    @JvmField
    val REPEATED = put(ProtobufKeywordToken("repeated"))
    @JvmField
    val REQUIRED = put(ProtobufKeywordToken("required"))
    @JvmField
    val RESERVED = put(ProtobufKeywordToken("reserved"))
    @JvmField
    val RETURNS = put(ProtobufKeywordToken("returns"))
    @JvmField
    val RPC = put(ProtobufKeywordToken("rpc"))
    @JvmField
    val SERVICE = put(ProtobufKeywordToken("service"))
    @JvmField
    val STREAM = put(ProtobufKeywordToken("stream"))
    @JvmField
    val SYNTAX = put(ProtobufKeywordToken("syntax"))
    @JvmField
    val TO = put(ProtobufKeywordToken("to"))
    @JvmField
    val TRUE = put(ProtobufKeywordToken("true"))
    @JvmField
    val WEAK = put(ProtobufKeywordToken("weak"))
}
