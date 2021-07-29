package io.kanro.idea.plugin.protobuf.sisyphus.name

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.string.toCamelCase

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