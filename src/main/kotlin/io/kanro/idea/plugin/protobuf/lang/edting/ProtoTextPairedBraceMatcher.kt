package io.kanro.idea.plugin.protobuf.lang.edting

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextPairedBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> {
        return Companion.pairs
    }

    override fun isPairedBracesAllowedBeforeType(
        lbraceType: IElementType,
        contextType: IElementType?,
    ): Boolean {
        return true
    }

    override fun getCodeConstructStart(
        file: PsiFile?,
        openingBraceOffset: Int,
    ): Int {
        return 0
    }

    companion object {
        private val pairs =
            arrayOf(
                BracePair(ProtoTextTokens.LBRACE, ProtoTextTokens.RBRACE, false),
                BracePair(ProtoTextTokens.LBRACK, ProtoTextTokens.RBRACK, false),
                BracePair(ProtoTextTokens.LPAREN, ProtoTextTokens.RPAREN, false),
                BracePair(ProtoTextTokens.LT, ProtoTextTokens.GT, false),
            )
    }
}
