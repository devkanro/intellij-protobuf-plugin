package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufNamedElement : ProtobufElement {
    fun name(): String?

    fun qualifiedName(): QualifiedName?
}
