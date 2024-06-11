package io.kanro.idea.plugin.protobuf.lang.psi.text.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.QualifiedName
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextSharpLineCommentImpl
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver

class ProtoTextHeaderMessageReference(comment: ProtoTextSharpLineCommentImpl) :
    PsiReferenceBase<ProtoTextSharpLineCommentImpl>(comment) {
    override fun resolve(): PsiElement? {
        val messageName = messageName()
        return ProtobufSymbolResolver.resolveInScope(
            element.schemaFile() ?: return null,
            QualifiedName.fromDottedString(messageName),
        ) as? ProtobufMessageDefinition
    }

    override fun getRangeInElement(): TextRange {
        val text = element.text
        val messageName = messageName()
        val start = text.indexOf(messageName)
        return TextRange.create(start, start + messageName.length)
    }

    override fun getVariants(): Array<Any> {
        val result = mutableListOf<Any>()
        val filter = ProtobufSymbolFilters.messageTypeName
        val messageName = messageName()
        val parentScope =
            messageName.substringBeforeLast('.', "").takeIf { it.isNotEmpty() }
                ?.let { QualifiedName.fromDottedString(it) } ?: QualifiedName.fromComponents()
        val scopeElement = element.schemaFile() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        ProtobufSymbolResolver.collectInScope(scopeElement, parentScope, filter).forEach {
            result += lookupFor(it, parentScope) ?: return@forEach
        }
        return result.toTypedArray()
    }

    private fun lookupFor(
        element: ProtobufElement,
        scope: QualifiedName,
    ): LookupElement? {
        val lookupElement = element as? LookupableElement ?: return null
        val fullName = scope.append(lookupElement.name).toString()
        return lookupElement.lookup(fullName)
    }

    fun messageName(): String {
        return element.text.substringAfter(ProtoTextFile.PROTOTEXT_HEADER_MESSAGE).trim()
    }
}
