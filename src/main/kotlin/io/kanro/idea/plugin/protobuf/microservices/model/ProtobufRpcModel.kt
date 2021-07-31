package io.kanro.idea.plugin.protobuf.microservices.model

import com.intellij.psi.SmartPsiElementPointer
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

class ProtobufRpcModel(private val pointer: SmartPsiElementPointer<ProtobufRpcDefinition>) {
    fun getPsi(): ProtobufRpcDefinition? {
        return pointer.element
    }
}
