package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.FormattingDocumentModelImpl
import com.intellij.psi.formatter.PsiBasedFormattingModel
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypes
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
        val file = element.containingFile
        return PsiBasedFormattingModel(
            file,
            ProtobufBlock(
                BlockType.BODY,
                element.node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                spacingBuilder(settings)
            ),
            FormattingDocumentModelImpl.createOn(file)
        )
    }

    companion object {
        private fun spacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
            val commonSettings: CommonCodeStyleSettings = settings.getCommonSettings(ProtobufLanguage)
            return SpacingBuilder(commonSettings)
                .withinPair(ProtobufTokens.LBRACE, ProtobufTokens.RBRACE)
                .spaceIf(commonSettings.SPACE_WITHIN_BRACES, false)
                .withinPair(ProtobufTokens.LBRACK, ProtobufTokens.RBRACK)
                .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, false)
                .withinPair(ProtobufTokens.LPAREN, ProtobufTokens.RPAREN)
                .spaceIf(commonSettings.SPACE_WITHIN_PARENTHESES, false)
                .before(ProtobufTokens.COMMA)
                .spaceIf(commonSettings.SPACE_BEFORE_COMMA)
                .after(ProtobufTokens.COMMA)
                .spaceIf(commonSettings.SPACE_AFTER_COMMA)
                .around(ProtobufTokens.ASSIGN)
                .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(ProtobufTokens.COLON)
                .spaceIf(commonSettings.SPACE_BEFORE_COLON)
                .after(ProtobufTokens.COLON)
                .spaceIf(commonSettings.SPACE_AFTER_COLON)
                .before(ProtobufTypes.MESSAGE_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.ENUM_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.SERVICE_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.ONEOF_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.EXTEND_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.RPC_BODY)
                .spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
                .before(ProtobufTypes.ENUM_VALUE_OPTION_BLOCK)
                .spaceIf(commonSettings.SPACE_BEFORE_METHOD_LBRACE)
                .before(ProtobufTypes.FIELD_OPTION_BLOCK)
                .spaceIf(commonSettings.SPACE_BEFORE_METHOD_LBRACE)
        }
    }
}
