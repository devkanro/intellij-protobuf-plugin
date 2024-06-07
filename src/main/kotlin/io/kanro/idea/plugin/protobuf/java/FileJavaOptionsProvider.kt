package io.kanro.idea.plugin.protobuf.java

import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufStubExternalProvider
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufStubSupport
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufFieldLikeStub

class FileJavaOptionsProvider : ProtobufStubExternalProvider {
    override fun mergeExternalData(
        element: ProtobufStubSupport<*, *>,
        external: MutableMap<String, String>,
    ) {
        when (element) {
            is ProtobufFieldLike -> {
                element.jsonName()?.let {
                    external["json_name"] = it
                }
            }
        }
    }

    override fun mergeExternalData(
        file: ProtobufFile,
        external: MutableMap<String, String>,
    ) {
        file.javaPackage()?.let {
            external["java_package"] = it
        }
        file.javaOuterClassname()?.let {
            external["java_outer_classname"] = it
        }
        file.javaMultipleFiles()?.let {
            external["java_multiple_files"] = it.toString()
        }
    }
}

fun ProtobufFieldLike.jsonName(): String? {
    return if (this is ProtobufOptionOwner) {
        options("json_name").lastOrNull()?.value()?.toString()
    } else {
        null
    }
}

fun ProtobufFieldLikeStub.jsonName(): String? {
    return if (this is io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<*>) {
        externalData("json_name")
    } else {
        null
    }
}

fun ProtobufFile.javaPackage(): String? {
    return options("java_package").lastOrNull()?.value()?.toString()
}

fun ProtobufFile.javaOuterClassname(): String? {
    return options("java_outer_classname").lastOrNull()?.value()?.toString()
}

fun ProtobufFile.javaMultipleFiles(): Boolean? {
    return options("java_multiple_files").lastOrNull()?.value() as? Boolean
}

fun io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub.javaPackage(): String? {
    return externalData("java_package")
}

fun io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub.javaOuterClassname(): String? {
    return externalData("java_outer_classname")
}

fun io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub.javaMultipleFiles(): Boolean? {
    return externalData("java_multiple_files")?.let { it == "true" }
}
