package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFileOption
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.value

class AipResourceReference(element: ProtobufStringValue) : PsiReferenceBase<ProtobufStringValue>(element) {
    override fun resolve(): PsiElement? {
        val resourceName = element.stringLiteral.text.trim('"')
        return ProtobufResourceResolver.resolveAbsolutely(element.file(), resourceName)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(1, element.textLength - 1)
    }

    override fun getVariants(): Array<Any> {
        return ProtobufResourceResolver.collectAbsolutely(element.file()).mapNotNull {
            when (it) {
                is ProtobufMessageDefinition -> {
                    val resourceName = it.resourceName() ?: return@mapNotNull null
                    LookupElementBuilder.create(
                        resourceName
                    ).withIcon(Icons.RESOURCE_MESSAGE).withPresentableText(resourceName)
                        .withInsertHandler(stringValueInsertHandler)
                }
                is ProtobufFileOption -> {
                    val resourceName =
                        it.value(AipOptions.resourceTypeField)?.stringValue?.value() ?: return@mapNotNull null
                    LookupElementBuilder.create(
                        resourceName
                    ).withIcon(Icons.RESOURCE_MESSAGE).withPresentableText(resourceName)
                        .withInsertHandler(stringValueInsertHandler)
                }
                else -> null
            }
        }.toTypedArray()
    }

    companion object {
        private val stringValueInsertHandler = SmartInsertHandler("\"")
    }
}
