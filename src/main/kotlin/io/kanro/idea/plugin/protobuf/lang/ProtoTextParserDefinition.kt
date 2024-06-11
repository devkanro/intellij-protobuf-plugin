package io.kanro.idea.plugin.protobuf.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import io.kanro.idea.plugin.protobuf.lang.lexer.ProtoTextLexer
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextTypes
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextFileImpl
import io.kanro.idea.plugin.protobuf.lang.psi.text.parser.ProtoTextParser
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return ProtoTextLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return ProtoTextParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return ProtoTextFile.Type
    }

    override fun getCommentTokens(): TokenSet {
        return comments
    }

    override fun getStringLiteralElements(): TokenSet {
        return string
    }

    override fun createElement(node: ASTNode): PsiElement {
        return ProtoTextTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return ProtoTextFileImpl(viewProvider)
    }

    companion object {
        val comments = TokenSet.create(ProtoTextTokens.SHARP_LINE_COMMENT)
        val string = TokenSet.create(ProtoTextTokens.STRING_LITERAL)
    }
}
