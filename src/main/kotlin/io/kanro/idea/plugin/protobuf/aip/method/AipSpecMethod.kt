package io.kanro.idea.plugin.protobuf.aip.method

import com.intellij.lang.annotation.AnnotationHolder
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

interface AipSpecMethod {
    val action: String

    fun annotate(method: ProtobufRpcDefinition, holder: AnnotationHolder)
}
