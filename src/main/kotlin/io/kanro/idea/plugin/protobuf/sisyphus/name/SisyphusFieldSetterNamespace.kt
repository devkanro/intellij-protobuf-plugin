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