package io.kanro.idea.plugin.protobuf.lang.edting

import com.intellij.psi.PsiElement
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.spellchecker.tokenizer.TokenizerBase
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextStringValue

class ProtoTextSpellcheckingStrategy : SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement?): Tokenizer<*> {
        if (element is ProtoTextStringValue) {
            return plainTextTokenizer
        }
        return super.getTokenizer(element)
    }

    companion object {
        private val plainTextTokenizer = TokenizerBase.create<PsiElement>(PlainTextSplitter.getInstance())
    }
}
