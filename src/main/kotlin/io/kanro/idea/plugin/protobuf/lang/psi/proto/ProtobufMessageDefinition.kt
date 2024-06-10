package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.firstItemOrNull
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.support.WellknownTypes
import java.util.Stack

/**
 * Resolve type of qualified field for a message definition.
 * It could be returning a [ProtobufMessageDefinition] for message field.
 * returning a [ProtobufEnumDefinition] for enum field.
 * returning a [ProtobufMapFieldDefinition] for map field.
 * returning a [ProtobufGroupDefinition] for group field.
 */
fun ProtobufMessageDefinition.resolveFieldType(
    qualifiedName: QualifiedName,
    jsonSpec: Boolean = false,
): ProtobufElement? {
    if (qualifiedName.components.isEmpty()) return this

    val q =
        Stack<String>().apply {
            addAll(qualifiedName.components.asReversed())
        }

    var scope: ProtobufScope = this

    while (q.isNotEmpty()) {
        val field = q.pop()

        if (jsonSpec) {
            if (scope is ProtobufMessageDefinition) {
                val message = scope.qualifiedName().toString()
                if (message == WellknownTypes.ANY && field == "@type") {
                    return scope.firstItemOrNull<ProtobufFieldLike> {
                        it.name() == "type_url"
                    }
                } else if (message in WellknownTypes.types) {
                    return null
                }
            }
        }

        val fieldDefinition =
            scope.firstItemOrNull<ProtobufFieldLike> {
                it.name() == field || it.jsonName() == field
            } ?: return null

        when (fieldDefinition) {
            is ProtobufFieldDefinition -> {
                val type = fieldDefinition.typeName.resolve() as? ProtobufElement ?: return null
                if (q.isEmpty()) return type
                scope = type as? ProtobufMessageDefinition ?: return null
            }

            is ProtobufMapFieldDefinition -> {
                if (q.isEmpty()) return fieldDefinition
                q.pop()
                val type =
                    fieldDefinition.typeNameList.lastOrNull()?.reference?.resolve() as? ProtobufElement
                        ?: return null
                if (q.isEmpty()) return type
                scope = type as? ProtobufMessageDefinition ?: return null
            }

            is ProtobufGroupDefinition -> {
                scope = fieldDefinition
                if (q.isEmpty()) return scope
            }
        }
    }
    return null
}

/**
 * Resolve qualified field for a message definition.
 * It could be returning any [ProtobufFieldLike].
 */
fun ProtobufMessageDefinition.resolveField(
    qualifiedName: QualifiedName,
    jsonSpec: Boolean = false,
): ProtobufFieldLike? {
    val field = qualifiedName.lastComponent ?: return null
    val parentField = qualifiedName.removeTail(1)
    val parentType = resolveFieldType(parentField, jsonSpec) ?: return null

    if (jsonSpec) {
        if (parentType is ProtobufMessageDefinition) {
            val message = parentType.qualifiedName().toString()
            if (message == WellknownTypes.ANY && field == "@type") {
                return parentType.firstItemOrNull<ProtobufFieldLike> {
                    it.name() == "type_url"
                }
            } else if (message in WellknownTypes.types) {
                return null
            }
        }
    }

    return when (parentType) {
        is ProtobufMessageDefinition -> {
            parentType.firstItemOrNull { it.name() == field || it.jsonName() == field }
        }

        is ProtobufGroupDefinition -> {
            parentType.firstItemOrNull { it.name() == field || it.jsonName() == field }
        }

        else -> null
    }
}

fun ProtobufFieldDefinition.repeated(): Boolean {
    return this.fieldLabel?.textMatches("repeated") == true
}

fun ProtobufGroupDefinition.repeated(): Boolean {
    return this.fieldLabel?.textMatches("repeated") == true
}

fun ProtobufFieldDefinition.required(): Boolean {
    return this.fieldLabel?.textMatches("required") == true
}

fun ProtobufGroupDefinition.required(): Boolean {
    return this.fieldLabel?.textMatches("required") == true
}

fun ProtobufFieldDefinition.optional(): Boolean {
    return this.fieldLabel?.textMatches("optional") == true
}

fun ProtobufGroupDefinition.optional(): Boolean {
    return this.fieldLabel?.textMatches("optional") == true
}
