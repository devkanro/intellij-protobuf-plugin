package io.kanro.idea.plugin.protobuf.aip.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.reference.AipResourceResolver
import io.kanro.idea.plugin.protobuf.lang.completion.AutoPopupInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.string.plural

class AipCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(ProtobufIdentifier::class.java)
                .withSuperParent(2, ProtobufRpcDefinition::class.java),
            MethodCompletionProvider,
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(ProtobufIdentifier::class.java)
                .withSuperParent(2, ProtobufRpcDefinition::class.java),
            ResourceMethodCompletionProvider,
        )
    }
}

object MethodCompletionProvider : CompletionProvider<CompletionParameters>() {
    val methods =
        setOf("Get", "List", "Update", "Create", "Delete", "BatchGet", "BatchUpdate", "BatchCreate", "BatchDelete")

    private val lookups = methods.map { methodElement(it) }

    private fun methodElement(methodType: String): LookupElement {
        return if (methodType.startsWith("Batch")) {
            LookupElementBuilder.create(methodType)
                .withTypeText("batch method")
                .withIcon(ProtobufIcons.RPC_METHOD)
                .withInsertHandler(AutoPopupInsertHandler)
        } else {
            LookupElementBuilder.create(methodType)
                .withTypeText("standard method")
                .withIcon(ProtobufIcons.RPC_METHOD)
                .withInsertHandler(AutoPopupInsertHandler)
        }
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val text = parameters.position.text
        if (methods.any { text.startsWith(it) }) {
            return
        }
        return result.addAllElements(lookups)
    }
}

object ResourceMethodCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val text = parameters.position.text
        val method =
            MethodCompletionProvider.methods.firstOrNull {
                text.startsWith(it)
            } ?: return
        val file = parameters.position.containingFile as? ProtobufFile ?: return

        result.addAllElements(
            AipResourceResolver.collectAbsolutely(file).mapNotNull {
                resourceElement(method, it as? ProtobufMessageDefinition)
            },
        )
    }

    private fun resourceElement(
        method: String,
        message: ProtobufMessageDefinition?,
    ): LookupElement? {
        val name = message?.name() ?: return null
        val pluralize = method.startsWith("Batch") || method == "List"
        val methodName = "$method${if (pluralize) name.plural() else name}"

        return LookupElementBuilder.create(message, methodName)
            .withIcon(ProtobufIcons.RESOURCE_MESSAGE)
            .withPresentableText(name)
            .withInsertHandler(MethodInsertHandler(method))
    }
}

class MethodInsertHandler(val method: String) : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val message = item.`object` as? ProtobufMessageDefinition ?: return
        val editor = context.editor
        val messageName = message.name()

        val pluralize = method.startsWith("Batch") || method == "List"
        val prefix = "${method}${if (pluralize) messageName?.plural() else messageName}"
        val request = "${prefix}Request"
        val response =
            when (method) {
                "Get",
                "Create",
                "Update",
                -> messageName

                "Delete" -> "google.protobuf.Empty"
                else -> "${prefix}Response"
            }
        EditorModificationUtil.insertStringAtCaret(editor, "($request) returns ($response)")
    }
}
