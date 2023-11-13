package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.openapi.extensions.ExtensionPointName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

interface ProtobufStubExternalProvider {
    companion object {
        var extensionPoint: ExtensionPointName<ProtobufStubExternalProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.stubExternalProvider")
    }

    fun mergeExternalData(
        element: ProtobufStubSupport<*, *>,
        external: MutableMap<String, String>,
    )

    fun mergeExternalData(
        file: ProtobufFile,
        external: MutableMap<String, String>,
    )
}
