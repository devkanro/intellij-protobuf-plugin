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
import io.kanro.idea.plugin.protobuf.lang.lexer.ProtobufLexer
import io.kanro.idea.plugin.protobuf.lang.parser.ProtobufParser
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypes
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufFileImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return ProtobufLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return ProtobufParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return ProtobufFileStub.Type
    }

    override fun getCommentTokens(): TokenSet {
        return comments
    }

    override fun getStringLiteralElements(): TokenSet {
        return string
    }

    override fun createElement(node: ASTNode): PsiElement {
        return ProtobufTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return ProtobufFileImpl(viewProvider)
    }

    companion object {
        val comments = TokenSet.create(ProtobufTokens.BLOCK_COMMENT, ProtobufTokens.LINE_COMMENT)
        val string = TokenSet.create(ProtobufTokens.STRING_LITERAL)
    }
}
