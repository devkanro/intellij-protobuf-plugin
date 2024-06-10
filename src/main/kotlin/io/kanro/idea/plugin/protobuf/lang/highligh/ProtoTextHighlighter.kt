package io.kanro.idea.plugin.protobuf.lang.highligh

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.lexer.ProtoTextLexer
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return ProtoTextLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(attributesMap[tokenType])
    }

    companion object {
        val IDENTIFIER =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_IDENTIFIER",
                DefaultLanguageHighlighterColors.IDENTIFIER,
            )
        val FIELD =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_FIELD",
                DefaultLanguageHighlighterColors.INSTANCE_FIELD,
            )
        val NUMBER =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_NUMBER",
                DefaultLanguageHighlighterColors.NUMBER,
            )
        val KEYWORD =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_KEYWORD",
                DefaultLanguageHighlighterColors.KEYWORD,
            )
        val STRING =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_STRING",
                DefaultLanguageHighlighterColors.STRING,
            )
        val ENUM_VALUE =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_ENUM_VALUE",
                DefaultLanguageHighlighterColors.CONSTANT,
            )
        val BLOCK_COMMENT =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_BLOCK_COMMENT",
                DefaultLanguageHighlighterColors.BLOCK_COMMENT,
            )
        val LINE_COMMENT =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_LINE_COMMENT",
                DefaultLanguageHighlighterColors.LINE_COMMENT,
            )
        val OPERATION_SIGN =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_OPERATION_SIGN",
                DefaultLanguageHighlighterColors.OPERATION_SIGN,
            )
        val BRACES =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_BRACES",
                DefaultLanguageHighlighterColors.BRACES,
            )
        val DOT = TextAttributesKey.createTextAttributesKey("TXTPB_DOT", DefaultLanguageHighlighterColors.DOT)
        val SEMICOLON =
            TextAttributesKey.createTextAttributesKey("TXTPB_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val COMMA = TextAttributesKey.createTextAttributesKey("TXTPB_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val PARENTHESES =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_PARENTHESES",
                DefaultLanguageHighlighterColors.PARENTHESES,
            )
        val BRACKETS =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_BRACKETS",
                DefaultLanguageHighlighterColors.BRACKETS,
            )

        // Invalid characters.
        val BAD_CHARACTER =
            TextAttributesKey.createTextAttributesKey(
                "TXTPB_BAD_CHARACTER",
                HighlighterColors.BAD_CHARACTER,
            )

        val attributesMap =
            mapOf(
                ProtoTextTokens.IDENTIFIER_LITERAL to IDENTIFIER,
                ProtoTextTokens.ASSIGN to OPERATION_SIGN,
                ProtoTextTokens.COLON to OPERATION_SIGN,
                ProtoTextTokens.COMMA to COMMA,
                ProtoTextTokens.DOT to DOT,
                ProtoTextTokens.GT to BRACES,
                ProtoTextTokens.LBRACE to BRACES,
                ProtoTextTokens.LBRACK to BRACKETS,
                ProtoTextTokens.LPAREN to PARENTHESES,
                ProtoTextTokens.LT to BRACES,
                ProtoTextTokens.MINUS to OPERATION_SIGN,
                ProtoTextTokens.PLUS to OPERATION_SIGN,
                ProtoTextTokens.RBRACE to BRACES,
                ProtoTextTokens.RBRACK to BRACKETS,
                ProtoTextTokens.RPAREN to PARENTHESES,
                ProtoTextTokens.SEMI to SEMICOLON,
                ProtoTextTokens.SLASH to OPERATION_SIGN,
                ProtoTextTokens.FLOAT_LITERAL to NUMBER,
                ProtoTextTokens.INTEGER_LITERAL to NUMBER,
                ProtoTextTokens.STRING_LITERAL to STRING,
                ProtoTextTokens.SHARP_LINE_COMMENT to LINE_COMMENT,
                ProtoTextTokens.BUILT_IN_TYPE to KEYWORD,
                ProtoTextTokens.FALSE to KEYWORD,
                ProtoTextTokens.TRUE to KEYWORD,
            )
    }
}
