package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement

interface ProtobufScopeItem : ProtobufElement {
    fun owner(): ProtobufScope? {
        return parentOfType()
    }
}
