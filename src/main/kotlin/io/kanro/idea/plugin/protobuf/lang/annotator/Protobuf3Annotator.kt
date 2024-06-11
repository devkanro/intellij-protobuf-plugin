package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofBody
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufVisitor
import io.kanro.idea.plugin.protobuf.lang.psi.proto.isFieldDefaultOption
import io.kanro.idea.plugin.protobuf.lang.psi.proto.weak
import io.kanro.idea.plugin.protobuf.lang.support.Options

class Protobuf3Annotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        if (element.containingFile.originalFile !is ProtobufFile) return
        val file = (element as? ProtobufElement)?.file() ?: return
        if (file.syntax() != "proto3") return

        element.accept(
            object : ProtobufVisitor() {
                override fun visitFieldDefinition(o: ProtobufFieldDefinition) {
                    val label = o.fieldLabel ?: return
                    if (o.parent is ProtobufOneofBody) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "OneOf field only support none label in proto3.",
                        )
                            .range(o.textRange)
                            .create()
                    } else {
                        if (label.textMatches("required")) {
                            holder.newAnnotation(
                                HighlightSeverity.ERROR,
                                "'required' field is not supported in proto3.",
                            )
                                .range(label.textRange)
                                .create()
                        }
                    }
                }

                override fun visitExtendDefinition(o: ProtobufExtendDefinition) {
                    val typename = o.typeName ?: return
                    val name = (typename.resolve() as? ProtobufMessageDefinition)?.qualifiedName() ?: return
                    if (Options.all.contains(name)) {
                        return
                    }
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Only Options can be extended in proto3.",
                    )
                        .range(typename.textRange)
                        .create()
                }

                override fun visitGroupDefinition(o: ProtobufGroupDefinition) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "'group' is not supported in proto3.",
                    )
                        .range(o.textRange)
                        .create()
                }

                override fun visitExtensionStatement(o: ProtobufExtensionStatement) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "'extension' is not supported in proto3.",
                    )
                        .range(o.textRange)
                        .create()
                }

                override fun visitImportStatement(o: ProtobufImportStatement) {
                    if (o.weak()) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "'weak' import is not supported in proto3.",
                        )
                            .range(o.importLabel?.textRange ?: o.textRange)
                            .create()
                    }
                }

                override fun visitOptionName(o: ProtobufOptionName) {
                    if (o.isFieldDefaultOption()) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "'default' option is not supported in proto3.",
                        )
                            .range(o.textRange)
                            .create()
                    }
                }

                override fun visitEnumDefinition(o: ProtobufEnumDefinition) {
                    val items = o.items()
                    val first = items.firstOrNull { it is ProtobufEnumValueDefinition }
                    val zeroDefinition =
                        items.firstOrNull { it is ProtobufEnumValueDefinition && it.number() == 0L } as? ProtobufEnumValueDefinition

                    if (zeroDefinition == null) {
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            "'zero' enum value is required in proto3.",
                        )
                            .range(o.textRange)
                            .create()
                    } else if (zeroDefinition != first) {
                        holder.newAnnotation(
                            HighlightSeverity.WEAK_WARNING,
                            "'${zeroDefinition.name()}' needs be first element in proto3 for compatibility with the proto2.",
                        )
                            .range(o.textRange)
                            .create()
                    }
                }

                override fun visitTypeName(o: ProtobufTypeName) {
                    val type = o.reference?.resolve()
                    if (type is ProtobufEnumDefinition) {
                        if (type.file().syntax() != "proto3") {
                            holder.newAnnotation(
                                HighlightSeverity.ERROR,
                                "Proto2 Enums is not supported in proto3.",
                            )
                                .range(o.textRange)
                                .create()
                        }
                    }
                }
            },
        )
    }
}
