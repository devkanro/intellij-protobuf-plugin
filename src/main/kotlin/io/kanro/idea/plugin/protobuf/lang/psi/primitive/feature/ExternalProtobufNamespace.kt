package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub

interface ExternalProtobufNamespace {
    fun name(element: ProtobufNamedElement?): String?

    fun name(element: ProtobufNamedStub?): String?

    fun scope(element: ProtobufScope?): QualifiedName?

    fun scope(element: ProtobufScopeStub?): QualifiedName?
}

fun ExternalProtobufNamespace.qualifiedName(element: ProtobufDefinition): QualifiedName? {
    return when(element) {
        is ProtobufScope -> scope(element)
        else -> scope(element.owner())?.append(name(element))
    }
}

fun ExternalProtobufNamespace.qualifiedName(element: ProtobufDefinitionStub): QualifiedName? {
    return when(element) {
        is ProtobufScopeStub -> scope(element)
        else -> scope(element.owner())?.append(name(element))
    }
}
