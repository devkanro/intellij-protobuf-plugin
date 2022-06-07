package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.grpc.index.MessageShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex

class GrpcTypeUrlReference(value: JsonStringLiteral) : PsiReferenceBase<JsonStringLiteral>(value) {
    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(1, element.textLength - 1)
    }

    override fun resolve(): PsiElement? {
        val property = element.parent as? JsonProperty ?: return null
        if (property.name != "@type") return null
        val type = element.value.substringAfterLast('/')
        return StubIndex.getElements(
            QualifiedNameIndex.key,
            type,
            element.project,
            GlobalSearchScope.allScope(element.project),
            ProtobufElement::class.java
        ).firstOrNull { it is ProtobufMessageDefinition } as? ProtobufMessageDefinition
    }

    override fun getVariants(): Array<Any> {
        val property = element.parent as? JsonProperty ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        if (property.name != "@type") return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val result = mutableListOf<Any>()
        val addedElements = mutableSetOf<ProtobufElement>()
        val pattern = rangeInElement.substring(element.text)
        getVariantsForShortName(pattern, result, addedElements)
        // getVariantsForQualifiedName(pattern, result, addedElements)
        return result.toTypedArray()
    }

    private fun getVariantsForShortName(
        pattern: String,
        result: MutableList<Any>,
        elements: MutableSet<ProtobufElement>
    ) {
        if (pattern.contains('.')) return
        val searchName = pattern.substringBefore(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
        val scope = GlobalSearchScope.allScope(element.project)
        val matcher = PlatformPatterns.string().contains(searchName)
        StubIndex.getInstance().getAllKeys(MessageShortNameIndex.key, element.project).asSequence().filter {
            matcher.accepts(it)
        }.flatMap {
            StubIndex.getElements(
                MessageShortNameIndex.key,
                it,
                element.project,
                scope,
                ProtobufMessageDefinition::class.java
            ).asSequence()
        }.forEach {
            if (it in elements) return@forEach
            result += it.lookup("type.googleapis.com/${it.qualifiedName()}")
                ?.withLookupString(it.name()!!) ?: return@forEach
            elements += it
        }
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}

