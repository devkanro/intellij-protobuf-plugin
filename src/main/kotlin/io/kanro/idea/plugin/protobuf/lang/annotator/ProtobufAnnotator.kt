package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.enum
import io.kanro.idea.plugin.protobuf.lang.psi.field
import io.kanro.idea.plugin.protobuf.lang.psi.isFieldDefaultOption
import io.kanro.idea.plugin.protobuf.lang.psi.message
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

class ProtobufAnnotator : Annotator {
    companion object {
        private val allowKeyType = setOf(
            BuiltInType.INT32.value(),
            BuiltInType.INT64.value(),
            BuiltInType.UINT32.value(),
            BuiltInType.UINT64.value(),
            BuiltInType.SINT32.value(),
            BuiltInType.SINT64.value(),
            BuiltInType.FIXED32.value(),
            BuiltInType.FIXED64.value(),
            BuiltInType.SFIXED32.value(),
            BuiltInType.SFIXED64.value(),
            BuiltInType.BOOL.value(),
            BuiltInType.STRING.value()
        )
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(object : ProtobufVisitor() {
            override fun visitMapField(o: ProtobufMapField) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
                NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
                val types = o.typeNameList
                if (types.size != 2) return
                val keyType = types[0].text
                if (keyType !in allowKeyType) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "$keyType is not a valid key type of map"
                    )
                        .range(types[0].textRange)
                        .create()
                }
            }

            override fun visitImportStatement(o: ProtobufImportStatement) {
                if (o.resolve() == null) {
                    val path = o.stringValue ?: return
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Imported file ${path.text} not found"
                    )
                        .range(path.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }

            override fun visitTypeName(o: ProtobufTypeName) {
                if (o.symbolNameList.size == 1 && BuiltInType.isBuiltInType(o.text)) return
                if (o.resolve() == null) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Symbol '${o.text}' not found"
                    )
                        .range(o.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }

            override fun visitBuiltInOptionName(o: ProtobufBuiltInOptionName) {
                if (!o.isFieldDefaultOption() && o.reference?.resolve() == null) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Built-in option '${o.text}' not found"
                    )
                        .range(o.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }

            override fun visitFieldName(o: ProtobufFieldName) {
                val message = o.message() ?: return
                val field = message.definitions().firstOrNull {
                    it.name() == element.text
                } as? ProtobufFieldDefinition
                if (field == null) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Field '${o.text}' not found"
                    )
                        .range(o.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }

            override fun visitEnumValue(o: ProtobufEnumValue) {
                val enum = o.enum() ?: return
                val value = enum.definitions().firstOrNull { it.name() == o.text }
                if (value == null) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Enum value '${o.text}' not found"
                    )
                        .range(o.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .create()
                }
            }

            override fun visitStringValue(o: ProtobufStringValue) {
                val field = when (val parent = o.parent) {
                    is ProtobufOptionAssign -> {
                        parent.optionName.field() as? ProtobufFieldDefinition ?: return
                    }
                    is ProtobufFieldAssign -> {
                        parent.fieldName.reference?.resolve() as? ProtobufFieldDefinition ?: return
                    }
                    else -> return
                }
                if (!field.typeName.textMatches("string")) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Type of field '${field.name}' is not string"
                    )
                        .range(o.textRange)
                        .create()
                }
            }

            override fun visitEnumDefinition(o: ProtobufEnumDefinition) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
                if (o.definitions().isEmpty()) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Enum must not be empty"
                    )
                        .range(o.body()?.textRange ?: o.textRange)
                        .create()
                }
            }

            override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
                NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
            }

            override fun visitMessageDefinition(o: ProtobufMessageDefinition) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
            }

            override fun visitServiceDefinition(o: ProtobufServiceDefinition) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
            }

            override fun visitGroupField(o: ProtobufGroupField) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
                NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
            }

            override fun visitEnumValueDefinition(o: ProtobufEnumValueDefinition) {
                ScopeTracker.tracker(o.owner() ?: return).visit(o, holder)
                NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
            }

            override fun visitReservedName(o: ProtobufReservedName) {
                ScopeTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
            }

            override fun visitReservedRange(o: ProtobufReservedRange) {
                NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
            }
        })
    }
}
