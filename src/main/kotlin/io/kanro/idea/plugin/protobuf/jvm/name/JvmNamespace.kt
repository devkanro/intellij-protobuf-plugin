package io.kanro.idea.plugin.protobuf.jvm.name

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.jvm.javaMultipleFiles
import io.kanro.idea.plugin.protobuf.jvm.javaOuterClassname
import io.kanro.idea.plugin.protobuf.jvm.javaPackage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.element.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMapFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import io.kanro.idea.plugin.protobuf.sisyphus.name.SisyphusNamespace
import io.kanro.idea.plugin.protobuf.string.toCamelCase

object JvmNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufRpcDefinition -> element.name()?.toCamelCase()
            is ProtobufFieldLike -> element.name()?.let { "get_$it" }?.toCamelCase()
            else -> element?.name()
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufRpcStub -> element.name()?.toCamelCase()
            is ProtobufFieldStub, is ProtobufMapFieldStub -> element.name()?.let { "get_$it" }?.toCamelCase()
            else -> element?.name()
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return when (element) {
            is ProtobufFile -> element.javaPackage()?.toQualifiedName() ?: element.scope()
            is ProtobufServiceDefinition -> scope(element.owner())?.append("${name(element)}Grpc")
                ?.append("${name(element)}ImplBase") ?: QualifiedName.fromComponents(
                "${name(element)}Grpc",
                "${name(element)}ImplBase"
            )
            is ProtobufDefinition -> scopeWithWrapper(element.owner())?.append(SisyphusNamespace.name(element))
                ?: QualifiedName.fromComponents(SisyphusNamespace.name(element))
            else -> null
        }
    }

    private fun scopeWithWrapper(scope: ProtobufScope?): QualifiedName? {
        return when (scope) {
            is ProtobufFile -> {
                if (scope.javaMultipleFiles() == true) {
                    scope(scope)
                } else {
                    val outerClass = scope.javaOuterClassname() ?: return null
                    scope(scope)?.append(outerClass) ?: QualifiedName.fromComponents(outerClass)
                }
            }
            null -> null
            else -> scope(scope)
        }
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return when (element) {
            is ProtobufFileStub -> element.javaPackage()?.toQualifiedName() ?: element.scope()
            is ProtobufServiceStub -> scope(element.owner())?.append("${name(element)}Grpc")
                ?.append("${name(element)}ImplBase") ?: QualifiedName.fromComponents(
                "${name(element)}Grpc",
                "${name(element)}ImplBase"
            )
            is ProtobufDefinitionStub -> scopeWithWrapper(element.owner())?.append(SisyphusNamespace.name(element))
                ?: QualifiedName.fromComponents(SisyphusNamespace.name(element))
            else -> null
        }
    }

    private fun scopeWithWrapper(scope: ProtobufScopeStub?): QualifiedName? {
        return when (scope) {
            is ProtobufFileStub -> {
                if (scope.javaMultipleFiles() == true) {
                    scope(scope)
                } else {
                    val outerClass = scope.javaOuterClassname() ?: return null
                    scope(scope)?.append(outerClass) ?: QualifiedName.fromComponents(outerClass)
                }
            }
            null -> null
            else -> scope(scope)
        }
    }
}