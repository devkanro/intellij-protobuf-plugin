package io.kanro.idea.plugin.protobuf.jvm

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufExternalNameProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.string.toCamelCase

class JvmNameProvider : ProtobufExternalNameProvider {
    override fun id(): String {
        return "jvm-name"
    }

    override fun externalName(element: ProtobufNamedElement): String? {
        return when (element) {
            is ProtobufRpcDefinition -> element.name()?.toCamelCase()
            else -> element.name()
        }
    }

    override fun externalPackage(file: ProtobufFile): QualifiedName? {
        return file.options("java_package").lastOrNull()?.value()?.stringValue?.value()?.let {
            QualifiedName.fromDottedString(it)
        }
    }
}