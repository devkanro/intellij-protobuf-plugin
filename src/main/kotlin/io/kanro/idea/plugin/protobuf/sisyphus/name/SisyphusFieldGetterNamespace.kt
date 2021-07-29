package io.kanro.idea.plugin.protobuf.sisyphus.name

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMapFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.string.toCamelCase

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