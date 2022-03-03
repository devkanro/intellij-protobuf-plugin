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
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypes
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val file = formattingContext.containingFile
        return PsiBasedFormattingModel(
            file,
            ProtobufBlock(
                BlockType.BODY,
                formattingContext.node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                spacingBuilder(formattingContext.codeStyleSettings)
            ),
            FormattingDocumentModelImpl.createOn(file)
        )
    }

    companion object {
        private fun spacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
            val commonSettings: CommonCodeStyleSettings = settings.getCommonSettings(ProtobufLanguage)
            val customSettings = settings.getCustomSettings(ProtobufCodeStyleSettings::class.java)
            return SpacingBuilder(commonSettings)
                .before(ProtobufTokens.SEMI)
                .none()
                .around(ProtobufTokens.ASSIGN)
                .spaces(1)
                .afterInside(ProtobufTokens.PACKAGE, ProtobufTypes.PACKAGE_STATEMENT)
                .spaces(1)
                .afterInside(ProtobufTokens.IMPORT, ProtobufTypes.IMPORT_STATEMENT)
                .spaces(1)
                .aroundInside(ProtobufTypes.IMPORT_LABEL, ProtobufTypes.IMPORT_STATEMENT)
                .spaces(1)
                .after(ProtobufTokens.OPTION)
                .spaces(1)
                .around(ProtobufTypes.IDENTIFIER)
                .spaces(1)
                .aroundInside(ProtobufTypes.TYPE_NAME, ProtobufTypes.EXTEND_DEFINITION)
                .spaces(1)
                .after(ProtobufTokens.STREAM)
                .spaces(1)
                .around(ProtobufTokens.RETURNS)
                .spaces(1)
                .around(ProtobufTokens.RESERVED)
                .spaces(1)
                .around(ProtobufTokens.TO)
                .spaces(1)
                .after(ProtobufTokens.COMMA)
                .spaces(1)
                .after(ProtobufTokens.EXTENSIONS)
                .spaces(1)
                .after(ProtobufTokens.EXTEND)
                .spaces(1)
                .after(ProtobufTokens.GROUP)
                .spaces(1)
                .after(ProtobufTokens.MAP)
                .none()
                .after(ProtobufTypes.FIELD_LABEL)
                .spaces(1)
                .between(ProtobufTypes.IMPORT_STATEMENT, ProtobufTypes.IMPORT_STATEMENT)
                .spacing(0, 0, 1, true, customSettings.KEEP_BLANK_LINES_BETWEEN_IMPORTS)
                .between(ProtobufTypes.FILE_OPTION, ProtobufTypes.FILE_OPTION)
                .spacing(0, 0, 1, true, customSettings.KEEP_BLANK_LINES_BETWEEN_FILE_OPTIONS)
                .after(ProtobufTypes.SYNTAX_STATEMENT)
                .blankLines(customSettings.BLANK_LINES_AFTER_SYNTAX)
                .after(ProtobufTypes.PACKAGE_STATEMENT)
                .blankLines(commonSettings.BLANK_LINES_AFTER_PACKAGE)
                .after(ProtobufTypes.IMPORT_STATEMENT)
                .blankLines(commonSettings.BLANK_LINES_AFTER_IMPORTS)
                .after(ProtobufTypes.FILE_OPTION)
                .blankLines(customSettings.BLANK_LINES_AFTER_FILE_OPTIONS)
                .between(ProtobufTypes.IDENTIFIER, ProtobufTypes.RPC_IO)
                .none()
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
