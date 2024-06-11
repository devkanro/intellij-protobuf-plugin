package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.FormattingDocumentModelImpl
import com.intellij.psi.formatter.PsiBasedFormattingModel
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val file = formattingContext.containingFile
        return PsiBasedFormattingModel(
            file,
            ProtobufBlock(
                BlockType.BODY,
                formattingContext.node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                spacingBuilder(formattingContext.codeStyleSettings),
            ),
            FormattingDocumentModelImpl.createOn(file),
        )
    }

    companion object {
        private fun spacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
            val commonSettings: CommonCodeStyleSettings = settings.getCommonSettings(ProtobufLanguage)
            return SpacingBuilder(commonSettings)
                .before(ProtoTextTokens.SEMI)
                .none()
                .around(ProtoTextTokens.ASSIGN)
                .spaces(1)
                .after(ProtoTextTokens.COMMA)
                .spaces(1)
                .withinPair(ProtoTextTokens.LBRACE, ProtoTextTokens.RBRACE)
                .spaceIf(commonSettings.SPACE_WITHIN_BRACES, false)
                .withinPair(ProtoTextTokens.LBRACK, ProtoTextTokens.RBRACK)
                .spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, false)
                .withinPair(ProtoTextTokens.LPAREN, ProtoTextTokens.RPAREN)
                .spaceIf(commonSettings.SPACE_WITHIN_PARENTHESES, false)
                .before(ProtoTextTokens.COMMA)
                .spaceIf(commonSettings.SPACE_BEFORE_COMMA)
                .after(ProtoTextTokens.COMMA)
                .spaceIf(commonSettings.SPACE_AFTER_COMMA)
                .around(ProtoTextTokens.ASSIGN)
                .spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(ProtoTextTokens.COLON)
                .spaceIf(commonSettings.SPACE_BEFORE_COLON)
                .after(ProtoTextTokens.COLON)
                .spaceIf(commonSettings.SPACE_AFTER_COLON)
        }
    }
}
