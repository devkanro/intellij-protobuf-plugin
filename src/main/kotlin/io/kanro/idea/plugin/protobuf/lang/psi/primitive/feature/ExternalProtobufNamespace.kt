package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub

interface ExternalProtobufNamespace {

    fun packageName(file: ProtobufFile): QualifiedName?

    fun packageName(file: ProtobufFileStub): QualifiedName?

    fun name(element: ProtobufNamedElement): String?

    fun name(element: ProtobufNamedStub): String?

    fun qualifiedName(element: ProtobufNamedElement): QualifiedName?

    fun qualifiedName(element: ProtobufNamedStub): QualifiedName?
}