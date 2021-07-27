package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufExternalNameProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

class SisyphusNameProvider : ProtobufExternalNameProvider {
    override fun id(): String {
        return "sisyphus-name"
    }

    override fun externalName(element: ProtobufNamedElement): String? {
        return when (element) {
            is ProtobufRpcDefinition -> element.name()?.toCamelCase()
            is ProtobufEnumValueDefinition -> {
                val enum = element.parentOfType<ProtobufEnumDefinition>() ?: return null
                val enumName = enum.name()?.toScreamingSnakeCase() ?: return null
                val valueName = element.name() ?: return null
                valueName.substringAfter("${enumName}_")
            }
            else -> element.name()
        }
    }

    override fun externalPackage(file: ProtobufFile): QualifiedName? {
        return file.options("java_package").lastOrNull()?.value()?.stringValue?.value()?.let {
            QualifiedName.fromDottedString(it)
        }
    }
}