package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.AddSpaceInsertHandler
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiErrorElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName

class ProtobufCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(PsiErrorElement::class.java)
                .withSuperParent(2, ProtobufFile::class.java),
            KeywordsProvider.topLevelKeywords
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufFieldDefinition::class.java)
                .andNot(PlatformPatterns.psiElement().afterLeaf(".")),
            KeywordsProvider.messageLevelKeywords
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufEnumValue::class.java)
                .andNot(PlatformPatterns.psiElement().afterLeaf("=")),
            KeywordsProvider.enumLevelKeywords
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(3, ProtobufServiceDefinition::class.java)
                .withParent(PsiErrorElement::class.java),
            KeywordsProvider.serviceLevelKeywords
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufTypeName::class.java)
                .andNot(PlatformPatterns.psiElement().afterLeaf(".")),
            BuiltInTypeProvider()
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufSyntaxStatement::class.java)
                .withParent(ProtobufStringValue::class.java),
            SyntaxProvider()
        )
    }
}

fun keywordElement(keyword: String): LookupElement {
    return LookupElementBuilder.create(keyword).withTypeText("keyword")
        .withInsertHandler(AddSpaceInsertHandler.INSTANCE)
}

fun builtInTypeElement(keyword: String): LookupElement {
    return LookupElementBuilder.create(keyword).withTypeText("built-in")
        .withInsertHandler(AddSpaceInsertHandler.INSTANCE)
}
