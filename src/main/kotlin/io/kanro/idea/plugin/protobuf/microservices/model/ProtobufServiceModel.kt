package io.kanro.idea.plugin.protobuf.microservices.model

import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class ProtobufServiceModel(private val pointer: SmartPsiElementPointer<ProtobufServiceDefinition>) {
    fun getMethods(): List<ProtobufRpcModel> {
        val service = pointer.element ?: return listOf()
        return service.items().mapNotNull {
            if (it !is ProtobufRpcDefinition) return@mapNotNull null
            return@mapNotNull ProtobufRpcModel(SmartPointerManager.createPointer(it))
        }
    }
}
