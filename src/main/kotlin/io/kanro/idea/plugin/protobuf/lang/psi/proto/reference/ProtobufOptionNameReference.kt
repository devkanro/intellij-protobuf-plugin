package io.kanro.idea.plugin.protobuf.lang.psi.proto.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.optionType
import io.kanro.idea.plugin.protobuf.lang.psi.realItems
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.support.Options

class ProtobufOptionNameReference(optionName: ProtobufOptionName) : PsiReferenceBase<ProtobufOptionName>(optionName) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(
            ref: PsiReference,
            incompleteCode: Boolean,
        ): PsiElement? {
            ref as ProtobufOptionNameReference
            val search = ref.element.symbolName?.text ?: return null
            val message = ref.ownerMessage() ?: return null

            if (message.qualifiedName() == Options.FIELD_OPTIONS.qualifiedName) {
                if (search == "default") {
                    return ref.element.parentOfType<ProtobufOptionOwner>()
                }
                if (search == "json_name") {
                    return ProtobufSymbolResolver.resolveAbsolutelyInFile(
                        ref.descriptor() ?: return null,
                        QualifiedName.fromDottedString("google.protobuf.FieldDescriptorProto.json_name"),
                    )
                }
            }

            return message.items().firstOrNull {
                (it as? NamedElement)?.name() == search
            }
        }
    }

    override fun resolve(): PsiElement? {
        return ResolveCache.getInstance(element.project).resolveWithCaching(this, Resolver, false, false)
    }

    private fun descriptor(): ProtobufFile? {
        return ProtobufRootResolver.findFile("google/protobuf/descriptor.proto", element).firstOrNull()?.let {
            PsiManager.getInstance(element.project).findFile(it) as? ProtobufFile
        }
    }

    private fun ownerMessage(): ProtobufMessageDefinition? {
        when (val parent = element.parent) {
            is ProtobufOptionAssign -> {
                return ProtobufSymbolResolver.resolveAbsolutelyInFile(
                    descriptor() ?: return null,
                    element.optionType()?.qualifiedName ?: return null,
                ) as? ProtobufMessageDefinition ?: return null
            }

            is ProtobufOptionName -> {
                val field = parent.resolve() as? ProtobufFieldDefinition ?: return null
                return field.typeName.resolve() as? ProtobufMessageDefinition ?: return null
            }

            else -> return null
        }
    }

    override fun getCanonicalText(): String {
        return element.text
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.symbolName?.textRangeInParent ?: TextRange.EMPTY_RANGE
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.symbolName?.identifierLiteral as? LeafPsiElement)?.replaceWithText(newElementName)
        return element
    }

    override fun getVariants(): Array<Any> {
        val message = ownerMessage() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val result =
            message.realItems().mapNotNull {
                (it as? ProtobufFieldDefinition)?.lookup()?.let {
                    when (it.psiElement?.reference?.resolve()) {
                        is ProtobufGroupDefinition,
                        is ProtobufMessageDefinition,
                        -> it

                        else -> it.withInsertHandler(fieldInsertHandler)
                    }
                }
            }.toMutableList()

        if (Options.FIELD_OPTIONS.qualifiedName == message.qualifiedName()) {
            result +=
                LookupElementBuilder.create("default").withTypeText("option").withIcon(ProtobufIcons.FIELD)
                    .withInsertHandler(fieldInsertHandler)
            result +=
                LookupElementBuilder.create("json_name").withTypeText("option").withIcon(ProtobufIcons.FIELD)
                    .withInsertHandler(fieldInsertHandler)
        }

        return result.toTypedArray()
    }

    companion object {
        private val fieldInsertHandler = SmartInsertHandler(" = ")
    }
}
