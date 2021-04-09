package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName

interface ProtobufScope : ProtobufElement {
    fun name(): String?

    fun scope(): QualifiedName?

    fun definitions(): Array<ProtobufDefinition>

    fun reservedNames(): Array<ProtobufReservedName>
}
