package io.kanro.idea.plugin.protobuf.lang.usage

import com.intellij.lang.HelpID
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import io.kanro.idea.plugin.protobuf.lang.ProtobufParserDefinition
import io.kanro.idea.plugin.protobuf.lang.lexer.ProtobufLexer
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufFindUsageProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            ProtobufLexer(),
            TokenSet.create(ProtobufTokens.IDENTIFIER_LITERAL),
            ProtobufParserDefinition.comments,
            ProtobufParserDefinition.string
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is ProtobufDefinition
    }

    override fun getHelpId(psiElement: PsiElement): String {
        return HelpID.FIND_OTHER_USAGES
    }

    override fun getType(element: PsiElement): String {
        return when (element) {
            is ProtobufDefinition -> element.type()
            else -> "unknown"
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        return getNodeText(element, true)
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        if (element !is ProtobufDefinition) return ""

        return if (useFullName) {
            element.qualifiedName().toString()
        } else {
            element.name() ?: ""
        }
    }
}
