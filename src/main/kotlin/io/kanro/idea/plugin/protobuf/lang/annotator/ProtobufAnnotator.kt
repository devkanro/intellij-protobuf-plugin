package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.highligh.ProtobufHighlighter
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufConstant
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.proto.field
import io.kanro.idea.plugin.protobuf.lang.psi.proto.range
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.value.IntegerValue
import io.kanro.idea.plugin.protobuf.lang.quickfix.AddImportFix
import io.kanro.idea.plugin.protobuf.lang.quickfix.RenameFix
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType
import io.kanro.idea.plugin.protobuf.string.case.CaseFormat
import io.kanro.idea.plugin.protobuf.string.toCase
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

class ProtobufAnnotator : Annotator {
    companion object {
        private val allowKeyType =
            setOf(
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
                BuiltInType.STRING.value(),
            )
    }

    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        element.accept(
            object : ProtobufVisitor() {
                private fun requireCase(
                    type: String,
                    o: ProtobufDefinition,
                    case: CaseFormat,
                ) {
                    val name = o.name() ?: return
                    if (name != name.toCase(case)) {
                        holder.newAnnotation(
                            HighlightSeverity.WARNING,
                            "$type should be ${case.name.toCase(case)}",
                        ).range(o.identifier()?.textRange ?: o.textRange).withFix(RenameFix(name.toCase(case))).create()
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
                            "$keyType is not a valid key type of map",
                        ).range(types[0].textRange).create()
                    }
                    visitFieldLike(o)
                }

                override fun visitImportStatement(o: ProtobufImportStatement) {
                    FileTracker.tracker(o.file()).visit(o, holder)
                }

                override fun visitTypeName(o: ProtobufTypeName) {
                    if (o.typeName == null && BuiltInType.isBuiltInType(o.text)) {
                        holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY).range(o.textRange)
                            .textAttributes(ProtobufHighlighter.KEYWORD).create()
                        return
                    }

                    if (o.parent !is ProtobufTypeName && o.resolve() == null) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Symbol '${o.text}' not found",
                        ).range(o.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                            .withFix(AddImportFix(o)).create()
                    }
                }

                override fun visitOptionName(o: ProtobufOptionName) {
                    val target: PsiElement = o.symbolName ?: o.extensionFieldName ?: return

                    if (o.resolve() == null) {
                        val isRoot = o.parent !is ProtobufOptionName
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "${if (isRoot) "Option" else "Field"} '${target.text}' not found",
                        ).range(target.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL).create()
                    }
                }

                override fun visitConstant(o: ProtobufConstant) {
                    val field =
                        when (val parent = o.parent) {
                            is ProtobufOptionAssign -> {
                                parent.optionName.field() as? ProtobufFieldDefinition ?: return
                            }

                            else -> return
                        }

                    val message =
                        when (val type = field.typeName.text) {
                            BuiltInType.BOOL.value() ->
                                if (o.booleanValue == null) {
                                    "Field \"${field.name()}\" required a boolean value"
                                } else {
                                    null
                                }

                            BuiltInType.STRING.value() ->
                                if (o.stringValue == null) {
                                    "Field \"${field.name()}\" required a string value"
                                } else {
                                    null
                                }

                            BuiltInType.FLOAT.value(),
                            BuiltInType.DOUBLE.value(),
                            ->
                                if (o.numberValue == null) {
                                    "Field \"${field.name()}\" required a number value"
                                } else {
                                    null
                                }

                            BuiltInType.UINT32.value(),
                            BuiltInType.UINT64.value(),
                            BuiltInType.FIXED32.value(),
                            BuiltInType.FIXED64.value(),
                            ->
                                if (o.numberValue == null) {
                                    "Field \"${field.name()}\" required a uint value"
                                } else {
                                    null
                                }

                            BuiltInType.INT32.value(),
                            BuiltInType.INT64.value(),
                            BuiltInType.SINT32.value(),
                            BuiltInType.SINT64.value(),
                            BuiltInType.SFIXED32.value(),
                            BuiltInType.SFIXED64.value(),
                            ->
                                if (o.numberValue == null) {
                                    "Field \"${field.name()}\" required a int value"
                                } else {
                                    null
                                }

                            else -> {
                                when (val typeDefinition = field.typeName.resolve()) {
                                    is ProtobufEnumDefinition ->
                                        if (o.enumValue == null) {
                                            "Field \"${field.name()}\" required a value of \"${typeDefinition.qualifiedName()}\""
                                        } else {
                                            null
                                        }

                                    is ProtobufMessageDefinition ->
                                        if (o.messageValue == null) {
                                            "Field \"${field.name()}\" required \"${typeDefinition.qualifiedName()}\" value"
                                        } else {
                                            null
                                        }

                                    else -> null
                                }
                            }
                        }

                    message?.let {
                        holder.newAnnotation(HighlightSeverity.ERROR, it).range(o.textRange).create()
                    }
                }

                override fun visitEnumDefinition(o: ProtobufEnumDefinition) {
                    o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                    if (o.items().isEmpty()) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Enum must not be empty",
                        ).range(o.body()?.textRange ?: o.textRange).create()
                    }
                }

                private fun visitFieldLike(o: ProtobufFieldLike) {
                    val value = o.number() ?: return
                    if (value < 1) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field number must be greater than 0",
                        ).range(o.findChild<IntegerValue>()?.textRange ?: o.textRange).create()
                    }
                }

                override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                    requireCase("Field name", o, CaseFormat.SNAKE_CASE)

                    o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                    o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }
                    visitExtendItem(o)
                    visitFieldLike(o)
                }

                private fun visitExtendItem(o: ProtobufElement) {
                    if (o.parentOfType<ProtobufGroupDefinition>() != null) return
                    val extendMessage =
                        o.parentOfType<ProtobufExtendDefinition>()?.typeName?.resolve() as? ProtobufMessageDefinition
                            ?: return

                    val insideExtension =
                        (o as? ProtobufNumbered)?.number()?.let { number ->
                            extendMessage.extensionRange().any {
                                it.range()?.contains(number) == true
                            }
                        }
                    if (insideExtension != true) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Extend field number must defined in extension range.",
                        ).range((o as? ProtobufNumbered)?.intValue()?.textRange ?: o.textRange).create()
                    }
                    ScopeTracker.tracker(extendMessage).visit(o as? ProtobufDefinition ?: return, holder)
                    NumberTracker.tracker(extendMessage).visit(o as? ProtobufNumbered ?: return, holder)
                }

                override fun visitGroupDefinition(o: ProtobufGroupDefinition) {
                    requireCase("Group name", o, CaseFormat.PASCAL_CASE)

                    o.owner()?.let { ScopeTracker.tracker(it).visit(o, holder) }
                    o.parentOfType<ProtobufNumberScope>()?.let { NumberTracker.tracker(it).visit(o, holder) }
                    visitExtendItem(o)
                    visitFieldLike(o)
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
                                "Value name of root enum should be start with enum name",
                            ).range(o.identifier()?.textRange ?: o.textRange).create()
                        }
                    }

                    val number = o.number() ?: return
                    if (number < 0) {
                        holder.newAnnotation(
                            HighlightSeverity.WARNING,
                            "Enum value number should be greater than or equal to 0",
                        ).range(o.integerValue?.textRange ?: o.textRange).create()
                    }
                }

                override fun visitReservedName(o: ProtobufReservedName) {
                    ScopeTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
                }

                override fun visitReservedRange(o: ProtobufReservedRange) {
                    NumberTracker.tracker(o.parentOfType() ?: return).visit(o, holder)
                }
            },
        )
    }
}
