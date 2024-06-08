package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.completion.AddImportInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.ComposedInsertHandler
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFileOption
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stringRangeInParent
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ResourceTypeIndex
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

class AipResourceReference(element: ProtobufStringValue) : PsiReferenceBase<ProtobufStringValue>(element) {
    override fun resolve(): PsiElement? {
        val resourceName = element.value()
        return AipResourceResolver.resolveAbsolutely(element.file(), resourceName)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.stringRangeInParent()
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val pattern = element.value()?.trim() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY

        getVariantsInCurrent(result, addedElements)
        getVariantsInStubIndex(pattern, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsInStubIndex(
        pattern: String,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ): Array<Any> {
        if (!pattern.endsWith(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val searchName = pattern.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
        val scope = ProtobufRootResolver.searchScope(element)
        val matcher = PlatformPatterns.string().contains(searchName)
        return StubIndex.getInstance().getAllKeys(ResourceTypeIndex.key, element.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(ResourceTypeIndex.key, it, element.project, scope, ProtobufElement::class.java)
                .asSequence()
        }.mapNotNull {
            if (it in elements) return@mapNotNull null
            result += lookup(it, true) ?: return@mapNotNull null
            elements += it
        }.toList().toTypedArray()
    }

    private fun getVariantsInCurrent(
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>,
    ) {
        AipResourceResolver.collectAbsolutely(element.file()).forEach {
            if (it in elements) return@forEach
            result += lookup(it, false) ?: return@forEach
            elements += it
        }
    }

    private fun lookup(
        element: ProtobufElement,
        needImport: Boolean,
    ): LookupElement? {
        val builder =
            when (element) {
                is ProtobufMessageDefinition -> {
                    val resourceName = element.resourceType() ?: return null
                    LookupElementBuilder.create(
                        resourceName,
                    ).withLookupString(resourceName.substringAfterLast('/'))
                        .withIcon(ProtobufIcons.RESOURCE_MESSAGE)
                        .withPresentableText(resourceName)
                }

                is ProtobufFileOption -> {
                    val resourceName =
                        element.value(AipOptions.resourceTypeField)?.toString() ?: return null
                    LookupElementBuilder.create(
                        resourceName,
                    ).withLookupString(resourceName.substringAfterLast('/'))
                        .withIcon(ProtobufIcons.RESOURCE_MESSAGE)
                        .withPresentableText(resourceName)
                }

                else -> return null
            }

        return if (needImport) {
            builder.withTailText("(${element.file().name()})")
                .withInsertHandler(
                    ComposedInsertHandler(stringValueInsertHandler, AddImportInsertHandler(element)),
                )
        } else {
            builder.withInsertHandler(
                ComposedInsertHandler(stringValueInsertHandler, AddImportInsertHandler(element)),
            )
        }
    }

    companion object {
        private val stringValueInsertHandler = SmartInsertHandler("\"")
    }
}
