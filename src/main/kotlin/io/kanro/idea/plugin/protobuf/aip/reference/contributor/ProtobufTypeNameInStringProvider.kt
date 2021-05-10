package io.kanro.idea.plugin.protobuf.aip.reference.contributor

import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHost
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufValueAssign
import io.kanro.idea.plugin.protobuf.string.splitToRange

class ProtobufTypeNameInStringProvider : ProtobufSymbolReferenceProvider {
    override fun hovers(element: ProtobufSymbolReferenceHost): ProtobufSymbolReferenceHover? {
        if (element !is ProtobufStringValue) return null
        val assign = element.parentOfType<ProtobufValueAssign>() ?: return null
        val targetField = assign.field()?.qualifiedName()
        if (targetField == AipOptions.lroMetadataName) return getHover(element)
        if (targetField == AipOptions.lroResponseName) return getHover(element)
        return null
    }

    private fun getHover(element: ProtobufStringValue): ProtobufSymbolReferenceHover {
        return CachedValuesManager.getCachedValue(element) {
            CachedValueProvider.Result.create(
                StringProtobufSymbolReferenceHover(element),
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }
}

class StringProtobufSymbolReferenceHover(val element: ProtobufStringValue) : ProtobufSymbolReferenceHover {
    private var text = element.text
    private val range: TextRange
    private val parts: List<ProtobufSymbolReferenceHover.SymbolPart>
    private val absolutely: Boolean

    init {
        var range = element.textRange
        var offset = 0
        if (text.startsWith('"')) {
            text = text.substring(1)
            offset += 1
            range = TextRange.create(range.startOffset + 1, range.endOffset)
        }
        if (text.startsWith('.')) {
            absolutely = true
            text = text.substring(1)
            offset += 1
            range = TextRange.create(range.startOffset + 1, range.endOffset)
        } else {
            absolutely = false
        }
        if (text.endsWith('"')) {
            text = text.substring(0, text.length - 1)
            range = TextRange.create(range.startOffset, range.endOffset - 1)
        }
        this.range = range

        parts = text.splitToRange('.').map {
            val realRange = it.shiftRight(offset)
            ProtobufSymbolReferenceHover.SymbolPart(realRange.startOffset, realRange.substring(element.text))
        }
    }

    override fun textRange(): TextRange {
        return range
    }

    override fun symbolParts(): List<ProtobufSymbolReferenceHover.SymbolPart> {
        return parts
    }

    override fun renamePart(index: Int, newName: String) {
        val leaf = (element.stringLiteral.node as LeafElement)
        val value = element.text
        val part = symbolParts()[index]
        val start = value.substring(0, part.startOffset)
        val end = value.substring(part.startOffset + part.value.length, value.length)
        leaf.replaceWithText("$start$newName$end")
    }

    override fun rename(newName: String) {
        val leaf = (element.stringLiteral.node as LeafElement)
        leaf.replaceWithText("\"$newName\"")
    }

    override fun absolutely(): Boolean {
        return absolutely
    }
}
