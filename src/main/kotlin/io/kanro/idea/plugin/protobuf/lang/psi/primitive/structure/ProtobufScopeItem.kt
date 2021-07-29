package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufScopeItem : ProtobufElement {
    fun owner(): ProtobufScope? {
        return parentOfType()
    }
}
