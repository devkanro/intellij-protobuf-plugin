package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub

interface ProtobufIndexProvider {
    companion object {
        var extensionPoint: ExtensionPointName<ProtobufIndexProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.indexProvider")

        fun buildIndex(
            stub: ProtobufStub<*>,
            sink: IndexSink,
        ) {
            extensionPoint.extensionList.forEach {
                it.buildIndex(stub, sink)
            }
        }
    }

    fun buildIndex(
        stub: ProtobufStub<*>,
        sink: IndexSink,
    )
}
