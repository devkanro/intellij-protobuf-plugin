package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiErrorElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumBody
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcBody
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName

class ProtobufCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(PsiErrorElement::class.java)
                .withSuperParent(2, ProtobufFile::class.java),
            KeywordsProvider.topLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufFieldDefinition::class.java)
                .inside(ProtobufTypeName::class.java)
                .andNot(PlatformPatterns.psiElement().afterLeaf(".")),
            KeywordsProvider.messageLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(PsiErrorElement::class.java)
                .inside(ProtobufEnumBody::class.java),
            KeywordsProvider.enumLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(PsiErrorElement::class.java)
                .inside(ProtobufRpcBody::class.java),
            KeywordsProvider.methodLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(2, ProtobufServiceDefinition::class.java)
                .afterLeaf(")"),
            KeywordsProvider.rpcLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(3, ProtobufServiceDefinition::class.java)
                .withParent(PsiErrorElement::class.java),
            KeywordsProvider.serviceLevelKeywords,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufTypeName::class.java)
                .andNot(PlatformPatterns.psiElement().afterLeaf(".")),
            BuiltInTypeProvider(),
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .inside(ProtobufSyntaxStatement::class.java)
                .withParent(ProtobufStringValue::class.java),
            SyntaxProvider(),
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(2, ProtobufFieldDefinition::class.java)
                .withParent(ProtobufIdentifier::class.java),
            FieldNameProvider,
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(2, ProtobufEnumValueDefinition::class.java)
                .withParent(ProtobufIdentifier::class.java),
            EnumValueNameProvider,
        )
    }
}
