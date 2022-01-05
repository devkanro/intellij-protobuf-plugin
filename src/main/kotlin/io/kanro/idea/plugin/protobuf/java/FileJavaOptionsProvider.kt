package io.kanro.idea.plugin.protobuf.java

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubExternalProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubSupport
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.stringValue
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufFieldLikeStub
import io.kanro.idea.plugin.protobuf.lang.psi.value

class FileJavaOptionsProvider : ProtobufStubExternalProvider {
    override fun mergeExternalData(element: ProtobufStubSupport<*, *>, external: MutableMap<String, String>) {
        when (element) {
            is ProtobufFieldLike -> {
                element.jsonName()?.let {
                    external["json_name"] = it
                }
            }
        }
    }

    override fun mergeExternalData(file: ProtobufFile, external: MutableMap<String, String>) {
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
        options("json_name").lastOrNull()?.value()?.stringValue()
    } else null
}

fun ProtobufFieldLikeStub.jsonName(): String? {
    return if (this is ProtobufStub<*>) {
        externalData("json_name")
    } else null
}

fun ProtobufFile.javaPackage(): String? {
    return options("java_package").lastOrNull()?.value()?.stringValue()
}

fun ProtobufFile.javaOuterClassname(): String? {
    return options("java_outer_classname").lastOrNull()?.value()?.stringValue()
}

fun ProtobufFile.javaMultipleFiles(): Boolean? {
    return options("java_multiple_files").lastOrNull()?.value()?.booleanValue?.value()
}

fun ProtobufFileStub.javaPackage(): String? {
    return externalData("java_package")
}

fun ProtobufFileStub.javaOuterClassname(): String? {
    return externalData("java_outer_classname")
}

fun ProtobufFileStub.javaMultipleFiles(): Boolean? {
    return externalData("java_multiple_files")?.let { it == "true" }
}
