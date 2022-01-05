package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.highligh.ProtobufHighlighter
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufArrayValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufConstant
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.enum
import io.kanro.idea.plugin.protobuf.lang.psi.field
import io.kanro.idea.plugin.protobuf.lang.psi.float
import io.kanro.idea.plugin.protobuf.lang.psi.int
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.message
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.range
import io.kanro.idea.plugin.protobuf.lang.psi.uint
import io.kanro.idea.plugin.protobuf.lang.quickfix.AddImportFix
import io.kanro.idea.plugin.protobuf.lang.quickfix.RenameFix
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufTypeNameReference
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType
import io.kanro.idea.plugin.protobuf.string.case.CaseFormat
import io.kanro.idea.plugin.protobuf.string.toCase
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

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
            private fun requireCase(type: String, o: ProtobufDefinition, case: CaseFormat) {
                val name = o.name() ?: return
                if (name != name.toCase(case)) {
                    holder.newAnnotation(
                        HighlightSeverity.WARNING,
                        "$type should be ${case.name.toCase(case)}"
                    )
                        .range(o.identifier()?.textRange ?: o.textRange)
                        .withFix(RenameFix(name.toCase(case)))
                        .create()
                }
            }

            override fun visitPackageStatement(o: ProtobufPackageStatement) {
                FileTracker.tracker(o.file()).visit(o, holder)
            }

            override fun visitMapFieldDefinition(o: ProtobufMapFieldDefinition) {
                requireCase("Field name", o, CaseFormat.SNAKE_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }
                visitExtendItem(o)
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
                FileTracker.tracker(o.file()).visit(o, holder)
            }

            override fun visitTypeName(o: ProtobufTypeName) {
                if (o.symbolNameList.size == 1 && BuiltInType.isBuiltInType(o.text)) {
                    holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                        .range(o.textRange)
                        .textAttributes(ProtobufHighlighter.KEYWORD)
                        .create()
                    return
                }

                if (o.reference?.resolve() == null) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Symbol '${o.text}' not found"
                    )
                        .range(o.textRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .withFix(AddImportFix(o))
                        .create()
                }

                o.references.forEach {
                    if (it !is ProtobufTypeNameReference) return@forEach
                    val result = it.resolve() ?: return@forEach
                    when (result) {
                        is ProtobufFieldLike -> holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                            .range(it.absoluteRange)
                            .textAttributes(ProtobufHighlighter.FIELD)
                            .create()
                        is ProtobufMessageDefinition -> holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                            .range(it.absoluteRange)
                            .textAttributes(ProtobufHighlighter.MESSAGE)
                            .create()
                        is ProtobufEnumDefinition -> holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                            .range(it.absoluteRange)
                            .textAttributes(ProtobufHighlighter.ENUM)
                            .create()
                        else -> holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                            .range(it.absoluteRange)
                            .textAttributes(ProtobufHighlighter.IDENTIFIER)
                            .create()
                    }
                }
            }

            override fun visitBuiltInOptionName(o: ProtobufBuiltInOptionName) {
                if (o.reference?.resolve() == null) {
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
                message.items<ProtobufFieldLike> {
                    if (it.name() == element.text) {
                        return
                    }
                }
                holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
                    .range(o.textRange)
                    .textAttributes(ProtobufHighlighter.FIELD)
                    .create()
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Field '${o.text}' not found"
                )
                    .range(o.textRange)
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    .create()
            }

            override fun visitEnumValue(o: ProtobufEnumValue) {
                val enum = o.enum() ?: return
                enum.items<ProtobufEnumValueDefinition> {
                    if (it.name() == element.text) {
                        return
                    }
                }
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Enum value '${o.text}' not found"
                )
                    .range(o.textRange)
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    .create()
            }

            override fun visitConstant(o: ProtobufConstant) {
                val parent = when (val parent = o.parent) {
                    is ProtobufArrayValue -> {
                        parent.parent.parent
                    }
                    else -> parent
                }

                val field = when (parent) {
                    is ProtobufOptionAssign -> {
                        parent.optionName.field() as? ProtobufFieldDefinition ?: return
                    }
                    is ProtobufFieldAssign -> {
                        parent.fieldName.reference?.resolve() as? ProtobufFieldDefinition ?: return
                    }
                    else -> return
                }

                if (o.arrayValue != null) {
                    if (field.fieldLabel?.textMatches("repeated") != true) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field \"${field.name()}\" is not a repeated value"
                        )
                            .range(o.textRange)
                            .create()
                    }
                    return
                }

                val message = when (val type = field.typeName.text) {
                    BuiltInType.BOOL.value() -> if (o.booleanValue == null) {
                        "Field \"${field.name()}\" required a boolean value"
                    } else null
                    BuiltInType.STRING.value() -> if (o.stringValueList.isEmpty()) {
                        "Field \"${field.name()}\" required a string value"
                    } else null
                    BuiltInType.FLOAT.value(),
                    BuiltInType.DOUBLE.value() -> if (o.numberValue?.float() == null) {
                        "Field \"${field.name()}\" required a number value"
                    } else null
                    BuiltInType.UINT32.value(),
                    BuiltInType.UINT64.value(),
                    BuiltInType.FIXED32.value(),
                    BuiltInType.FIXED64.value() -> if (o.numberValue?.uint() == null) {
                        "Field \"${field.name()}\" required a uint value"
                    } else null
                    BuiltInType.INT32.value(),
                    BuiltInType.INT64.value(),
                    BuiltInType.SINT32.value(),
                    BuiltInType.SINT64.value(),
                    BuiltInType.SFIXED32.value(),
                    BuiltInType.SFIXED64.value() -> if (o.numberValue?.int() == null) {
                        "Field \"${field.name()}\" required a int value"
                    } else null
                    else -> {
                        when (val typeDefinition = field.typeName.reference?.resolve()) {
                            is ProtobufEnumDefinition -> if (o.enumValue == null) {
                                "Field \"${field.name()}\" required a value of \"${typeDefinition.qualifiedName()}\""
                            } else null
                            is ProtobufMessageDefinition -> if (o.messageValue == null) {
                                "Field \"${field.name()}\" required \"${typeDefinition.qualifiedName()}\" value"
                            } else null
                            else -> null
                        }
                    }
                }

                message?.let {
                    holder.newAnnotation(HighlightSeverity.ERROR, it)
                        .range(o.textRange)
                        .create()
                }
            }

            override fun visitEnumDefinition(o: ProtobufEnumDefinition) {
                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                if (o.items().isEmpty()) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Enum must not be empty"
                    )
                        .range(o.body()?.textRange ?: o.textRange)
                        .create()
                }
            }

            override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                requireCase("Field name", o, CaseFormat.SNAKE_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }
                visitExtendItem(o)
            }

            private fun visitExtendItem(o: ProtobufElement) {
                val extendMessage =
                    o.parentOfType<ProtobufExtendDefinition>()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
                        ?: return

                val insideExtension = (o as? ProtobufNumbered)?.number()?.let { number ->
                    extendMessage.extensionRange().any {
                        it.range()?.contains(number) == true
                    }
                }
                if (insideExtension != true) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Extend field number must defined in extension range."
                    )
                        .range((o as? ProtobufNumbered)?.intValue()?.textRange ?: o.textRange)
                        .create()
                }
                ScopeTracker.tracker(extendMessage).visit(o as? ProtobufDefinition ?: return, holder)
                NumberTracker.tracker(extendMessage).visit(o as? ProtobufNumbered ?: return, holder)
            }

            override fun visitGroupDefinition(o: ProtobufGroupDefinition) {
                requireCase("Group name", o, CaseFormat.PASCAL_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }
                visitExtendItem(o)
            }

            override fun visitMessageDefinition(o: ProtobufMessageDefinition) {
                requireCase("Message name", o, CaseFormat.PASCAL_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
            }

            override fun visitServiceDefinition(o: ProtobufServiceDefinition) {
                requireCase("Message name", o, CaseFormat.PASCAL_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
            }

            override fun visitRpcDefinition(o: ProtobufRpcDefinition) {
                requireCase("Method name", o, CaseFormat.PASCAL_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
            }

            override fun visitEnumValueDefinition(o: ProtobufEnumValueDefinition) {
                requireCase("Enum value name", o, CaseFormat.SCREAMING_SNAKE_CASE)

                o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }

                val enumName = o.name() ?: return
                if (o.owner()?.owner() is ProtobufFile) {
                    val parentName = o.owner()?.name() ?: return
                    if (!enumName.startsWith(parentName.toScreamingSnakeCase())) {
                        holder.newAnnotation(
                            HighlightSeverity.WARNING,
                            "Value name of root enum should be start with enum name"
                        )
                            .range(o.identifier()?.textRange ?: o.textRange)
                            .create()
                    }
                }
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
