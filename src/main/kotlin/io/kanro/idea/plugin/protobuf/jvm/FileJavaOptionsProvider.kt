package io.kanro.idea.plugin.protobuf.jvm

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubExternalProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubSupport
import io.kanro.idea.plugin.protobuf.lang.psi.value

class FileJavaOptionsProvider : ProtobufStubExternalProvider {
    override fun mergeExternalData(element: ProtobufStubSupport<*, *>, external: MutableMap<String, String>) {
    }

    override fun mergeExternalData(file: ProtobufFile, external: MutableMap<String, String>) {
        file.javaPackage()?.let {
            external["java_package"] = it
        }
        file.javaOuterClassname()?.let {
            external["java_outer_classname"] = it
        }
        file.options("java_multiple_files").lastOrNull()?.value()?.booleanValue?.value()?.let {
            external["java_multiple_files"] = it.toString()
        }
    }
}

fun ProtobufFile.javaPackage(): String? {
    return options("java_package").lastOrNull()?.value()?.stringValue?.value()
}

fun ProtobufFile.javaOuterClassname(): String? {
    return options("java_outer_classname").lastOrNull()?.value()?.stringValue?.value()
}
