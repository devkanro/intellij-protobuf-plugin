package io.kanro.idea.plugin.protobuf.sisyphus.name

import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.jvm.javaPackage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumValueStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMapFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

object SisyphusNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufRpcDefinition -> element.name()?.toCamelCase()
            is ProtobufFieldLike -> element.name()?.toCamelCase()
            is ProtobufEnumValueDefinition -> {
                val enumValue =
                    element.parentOfType<ProtobufEnumDefinition>()?.name()?.toScreamingSnakeCase() ?: return null
                element.name()?.substringAfter("${enumValue}_")
            }
            else -> element?.name()
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufRpcStub -> element.name()?.toCamelCase()
            is ProtobufFieldStub -> element.name()?.toCamelCase()
            is ProtobufMapFieldStub -> element.name()?.toCamelCase()
            is ProtobufEnumValueStub -> {
                val enumValue = (element.parentStub as ProtobufEnumStub).name()?.toScreamingSnakeCase() ?: return null
                element.name()?.substringAfter("${enumValue}_")
            }
            else -> element?.name()
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return when (element) {
            is ProtobufFile -> element.javaPackage()?.toQualifiedName()
            is ProtobufDefinition -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return when (element) {
            is ProtobufFileStub -> element.externalData("java_package")?.toQualifiedName()
            is ProtobufDefinitionStub -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }
}

