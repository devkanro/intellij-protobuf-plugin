package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

object EnumValueNameProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val element = parameters.position
        val name = element.parentOfType<ProtobufEnumDefinition>()?.name() ?: return

        result.addElement(
            LookupElementBuilder.create("$name unspecified".toScreamingSnakeCase())
                .withTypeText("enum value")
                .withInsertHandler(enumValueNumberInserter(0)),
        )
        result.addElement(
            LookupElementBuilder.create(name.toScreamingSnakeCase())
                .withTypeText("enum value")
                .withInsertHandler(SmartInsertHandler("_")),
        )
    }

    private fun enumValueNumberInserter(number: Long): InsertHandler<LookupElement> {
        return SmartInsertHandler(" = $number;", -1)
    }
}
