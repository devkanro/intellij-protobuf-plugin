package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.AddSpaceInsertHandler
import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiErrorElement
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

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

class KeywordsProvider(keywords: List<String>) : CompletionProvider<CompletionParameters>() {
    private val keywordElements = keywords.map { keywordElement(it) }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addAllElements(keywordElements)
    }

    companion object {
        val topLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "syntax", "package", "import", "option", "message", "enum", "service", "extend"
                )
            )

        val messageLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "message", "enum", "extend", "oneof", "group", "extensions", "reserved",
                    "repeated", "optional", "required"
                )
            )

        val enumLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "reserved"
                )
            )

        val serviceLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "rpc"
                )
            )
    }
}

class BuiltInTypeProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addAllElements(builtInTypes)
    }

    companion object {
        private val builtInTypes = BuiltInType.values().map {
            builtInTypeElement(it.value())
        }
    }
}

class SyntaxProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addElement(
            LookupElementBuilder.create("proto2")
                .withTypeText("syntax")
                .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
        )
        result.addElement(
            LookupElementBuilder.create("proto3")
                .withTypeText("syntax")
                .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
        )
    }
}

class ProtobufCompletionConfidence : CompletionConfidence()

fun keywordElement(keyword: String): LookupElement {
    return LookupElementBuilder.create(keyword).withTypeText("keyword")
        .withInsertHandler(AddSpaceInsertHandler.INSTANCE)
}

fun builtInTypeElement(keyword: String): LookupElement {
    return LookupElementBuilder.create(keyword).withTypeText("built-in")
        .withInsertHandler(AddSpaceInsertHandler.INSTANCE)
}
