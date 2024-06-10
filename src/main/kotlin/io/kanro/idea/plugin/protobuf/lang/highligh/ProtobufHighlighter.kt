package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.lexer.ProtobufLexer
import io.kanro.idea.plugin.protobuf.lang.psi.proto.token.ProtobufTokens

class ProtobufHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return ProtobufHighlightLexer(ProtobufLexer())
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(attributesMap[tokenType])
    }

    companion object {
        val IDENTIFIER =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_IDENTIFIER",
                DefaultLanguageHighlighterColors.IDENTIFIER,
            )
        val FIELD =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_FIELD",
                DefaultLanguageHighlighterColors.INSTANCE_FIELD,
            )
        val MESSAGE =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_MESSAGE",
                DefaultLanguageHighlighterColors.CLASS_NAME,
            )
        val SERVICE =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_SERVICE",
                DefaultLanguageHighlighterColors.INTERFACE_NAME,
            )
        val METHOD =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_METHOD",
                DefaultLanguageHighlighterColors.FUNCTION_DECLARATION,
            )
        val ENUM =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_ENUM",
                DefaultLanguageHighlighterColors.INTERFACE_NAME,
            )
        val NUMBER =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_NUMBER",
                DefaultLanguageHighlighterColors.NUMBER,
            )
        val KEYWORD =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_KEYWORD",
                DefaultLanguageHighlighterColors.KEYWORD,
            )
        val STRING =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_STRING",
                DefaultLanguageHighlighterColors.STRING,
            )
        val ENUM_VALUE =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_ENUM_VALUE",
                DefaultLanguageHighlighterColors.CONSTANT,
            )
        val BLOCK_COMMENT =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_BLOCK_COMMENT",
                DefaultLanguageHighlighterColors.BLOCK_COMMENT,
            )
        val DOC_COMMENT =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_DOC_COMMENT",
                DefaultLanguageHighlighterColors.DOC_COMMENT,
            )
        val LINE_COMMENT =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_LINE_COMMENT",
                DefaultLanguageHighlighterColors.LINE_COMMENT,
            )
        val OPERATION_SIGN =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_OPERATION_SIGN",
                DefaultLanguageHighlighterColors.OPERATION_SIGN,
            )
        val BRACES =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_BRACES",
                DefaultLanguageHighlighterColors.BRACES,
            )
        val DOT = TextAttributesKey.createTextAttributesKey("PROTO_DOT", DefaultLanguageHighlighterColors.DOT)
        val SEMICOLON =
            TextAttributesKey.createTextAttributesKey("PROTO_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val COMMA = TextAttributesKey.createTextAttributesKey("PROTO_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val PARENTHESES =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_PARENTHESES",
                DefaultLanguageHighlighterColors.PARENTHESES,
            )
        val BRACKETS =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_BRACKETS",
                DefaultLanguageHighlighterColors.BRACKETS,
            )

        // Invalid characters.
        val BAD_CHARACTER =
            TextAttributesKey.createTextAttributesKey(
                "PROTO_BAD_CHARACTER",
                HighlighterColors.BAD_CHARACTER,
            )

        val attributesMap =
            mapOf(
                ProtobufTokens.IDENTIFIER_LITERAL to IDENTIFIER,
                ProtobufTokens.ASSIGN to OPERATION_SIGN,
                ProtobufTokens.COLON to OPERATION_SIGN,
                ProtobufTokens.COMMA to COMMA,
                ProtobufTokens.DOT to DOT,
                ProtobufTokens.GT to BRACES,
                ProtobufTokens.LBRACE to BRACES,
                ProtobufTokens.LBRACK to BRACKETS,
                ProtobufTokens.LPAREN to PARENTHESES,
                ProtobufTokens.LT to BRACES,
                ProtobufTokens.MINUS to OPERATION_SIGN,
                ProtobufTokens.PLUS to OPERATION_SIGN,
                ProtobufTokens.RBRACE to BRACES,
                ProtobufTokens.RBRACK to BRACKETS,
                ProtobufTokens.RPAREN to PARENTHESES,
                ProtobufTokens.SEMI to SEMICOLON,
                ProtobufTokens.SLASH to OPERATION_SIGN,
                ProtobufTokens.FLOAT_LITERAL to NUMBER,
                ProtobufTokens.INTEGER_LITERAL to NUMBER,
                ProtobufTokens.STRING_LITERAL to STRING,
                ProtobufTokens.BLOCK_COMMENT to BLOCK_COMMENT,
                ProtobufTokens.LINE_COMMENT to LINE_COMMENT,
                ProtobufTokens.BUILT_IN_TYPE to KEYWORD,
                ProtobufTokens.DEFAULT to KEYWORD,
                ProtobufTokens.ENUM to KEYWORD,
                ProtobufTokens.EXTEND to KEYWORD,
                ProtobufTokens.EXTENSIONS to KEYWORD,
                ProtobufTokens.FALSE to KEYWORD,
                ProtobufTokens.GROUP to KEYWORD,
                ProtobufTokens.IMPORT to KEYWORD,
                ProtobufTokens.JSON_NAME to KEYWORD,
                ProtobufTokens.MAP to KEYWORD,
                ProtobufTokens.MAX to KEYWORD,
                ProtobufTokens.MESSAGE to KEYWORD,
                ProtobufTokens.ONEOF to KEYWORD,
                ProtobufTokens.OPTION to KEYWORD,
                ProtobufTokens.OPTIONAL to KEYWORD,
                ProtobufTokens.PACKAGE to KEYWORD,
                ProtobufTokens.PUBLIC to KEYWORD,
                ProtobufTokens.REPEATED to KEYWORD,
                ProtobufTokens.REQUIRED to KEYWORD,
                ProtobufTokens.RESERVED to KEYWORD,
                ProtobufTokens.RETURNS to KEYWORD,
                ProtobufTokens.RPC to KEYWORD,
                ProtobufTokens.SERVICE to KEYWORD,
                ProtobufTokens.STREAM to KEYWORD,
                ProtobufTokens.SYNTAX to KEYWORD,
                ProtobufTokens.TO to KEYWORD,
                ProtobufTokens.TRUE to KEYWORD,
                ProtobufTokens.WEAK to KEYWORD,
            )
    }
}
