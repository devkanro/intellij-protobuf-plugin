package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.repeated
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextAnyName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextElement
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextField
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.text.enum
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderFileReference
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderMessageReference
import io.kanro.idea.plugin.protobuf.lang.psi.text.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.value.ArrayValue
import io.kanro.idea.plugin.protobuf.lang.support.WellknownTypes

class ProtoTextAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        if (element !is ProtoTextElement) {
            return
        }
        element.accept(
            object : ProtoTextVisitor() {
                override fun visitComment(comment: PsiComment) {
                    when (val reference = comment.reference) {
                        is ProtoTextHeaderFileReference -> {
                            if (reference.resolve() == null) {
                                holder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Schema file '${reference.fileName()}' not found",
                                ).range(reference.absoluteRange).highlightType(ProblemHighlightType.WARNING).create()
                            }
                        }

                        is ProtoTextHeaderMessageReference -> {
                            if (reference.resolve() == null) {
                                holder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Message '${reference.messageName()}' not found",
                                ).range(reference.absoluteRange).highlightType(ProblemHighlightType.WARNING).create()
                            }
                        }
                    }
                }

                override fun visitEnumValue(o: ProtoTextEnumValue) {
                    if (o.file()?.schema() == null) return

                    val enum = o.enum() ?: return
                    enum.items<ProtobufEnumValueDefinition> {
                        if (it.name() == o.text) {
                            return
                        }
                    }
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Enum value '${o.text}' not found",
                    ).range(o.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL).create()
                }

                override fun visitFieldName(o: ProtoTextFieldName) {
                    if (o.file()?.schema() == null) return

                    if (o.resolve() == null) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field '${o.text}' not existed",
                        ).range(o.textRange).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL).create()
                    }
                }

                override fun visitField(o: ProtoTextField) {
                    visitValueAssign(o)
                }

                private fun visitValueAssign(o: ValueAssign) {
                    val field = o.field() ?: return
                    val valueElement = o.valueElement() ?: return

                    if (valueElement is ArrayValue) {
                        when (field) {
                            is ProtobufFieldDefinition -> {
                                if (!field.repeated()) {
                                    holder.newAnnotation(
                                        HighlightSeverity.ERROR,
                                        "Field '${field.name()}' is not a repeated field",
                                    ).range(field.textRange).create()
                                }
                            }

                            is ProtobufGroupDefinition -> {
                                if (!field.repeated()) {
                                    holder.newAnnotation(
                                        HighlightSeverity.ERROR,
                                        "Field '${field.name()}' is not a repeated field",
                                    ).range(field.textRange).create()
                                }
                            }
                        }

                        valueElement.values().forEach {
                            checkCompatibleType(it, field)
                        }
                    } else {
                        checkCompatibleType(valueElement, field)
                    }
                }

                private fun checkCompatibleType(
                    value: ValueElement<*>,
                    field: ProtobufFieldLike,
                ) {
                    val fieldType = field.fieldValueType()
                    val valueType = value.valueType()
                    if (valueType != fieldType) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field '${field.fieldName()}' required ${fieldType.name.lowercase()} value, but got ${valueType.name.lowercase()} value",
                        ).range(value.textRange).create()
                    }
                }

                override fun visitAnyName(o: ProtoTextAnyName) {
                    if (o.file()?.schema() == null) return

                    val parentField = o.parentOfType<ProtoTextField>()?.parentOfType<ProtoTextField>()?.field()

                    if (parentField !is ProtobufFieldDefinition || (parentField.typeName.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.toString() != WellknownTypes.ANY) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "Field '${parentField?.name()}' is not a field with Any type",
                        ).range(o.textRange).highlightType(ProblemHighlightType.ERROR).create()
                    }
                }
            },
        )
    }
}
