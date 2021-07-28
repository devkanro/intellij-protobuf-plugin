package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.jvm.javaPackage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
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
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub
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

object SisyphusClientNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufRpcDefinition -> element.name()?.toCamelCase()
            else -> null
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufRpcStub -> element.name()?.toCamelCase()
            else -> null
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return when (element) {
            is ProtobufFile -> SisyphusNamespace.scope(element)
            is ProtobufServiceDefinition -> SisyphusNamespace.scope(element)?.append("Client")
            else -> null
        }
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return when (element) {
            is ProtobufFileStub -> SisyphusNamespace.scope(element)
            is ProtobufServiceStub -> SisyphusNamespace.scope(element)?.append("Client")
            else -> null
        }
    }
}

object SisyphusFieldGetterNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufFieldLike -> element.name()?.let { "get_$it" }?.toCamelCase()
            else -> null
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufFieldStub, is ProtobufMapFieldStub -> element.name()?.let { "get_$it" }?.toCamelCase()
            else -> null
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return SisyphusNamespace.scope(element)
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return SisyphusNamespace.scope(element)
    }
}

object SisyphusFieldSetterNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufFieldLike -> element.name()?.let { "set_$it" }?.toCamelCase()
            else -> null
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufFieldStub, is ProtobufMapFieldStub -> element.name()?.let { "set_$it" }?.toCamelCase()
            else -> null
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return SisyphusMutableMessageNamespace.scope(element)
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return SisyphusMutableMessageNamespace.scope(element)
    }
}

object SisyphusMutableMessageNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufMessageDefinition -> "Mutable${element.name()}"
            else -> null
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufMessageStub -> "Mutable${element.name()}"
            else -> null
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return when (element) {
            is ProtobufFile -> element.javaPackage()?.toQualifiedName()?.append("internal")
            is ProtobufDefinition -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return when (element) {
            is ProtobufFileStub -> element.externalData("java_package")?.toQualifiedName()?.append("internal")
            is ProtobufDefinitionStub -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }
}
