package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

interface ProtobufExternalNameProvider {
    companion object {
        var extensionPoint: ExtensionPointName<ProtobufExternalNameProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.externalNameProvider")

        fun externalName(id: String, element: ProtobufNamedElement): String? {
            extensionPoint.extensionList.forEach {
                if (it.id() == id) {
                    return it.externalName(element)
                }
            }
            return null
        }

        fun externalPackage(id: String, file: ProtobufFile): QualifiedName? {
            extensionPoint.extensionList.forEach {
                if (it.id() == id) {
                    return it.externalPackage(file)
                }
            }
            return null
        }
    }

    fun id(): String

    fun externalName(element: ProtobufNamedElement): String? {
        return element.name()
    }

    fun externalPackage(file: ProtobufFile): QualifiedName? {
        return file.scope()
    }
}

class ProtobufStubExternalNameProvider : ProtobufStubExternalProvider {
    override fun mergeExternalData(element: ProtobufStubSupport<*, *>, external: MutableMap<String, String>) {
        if (element !is ProtobufNamedElement) return

        ProtobufExternalNameProvider.extensionPoint.extensionList.forEach {
            it.externalName(element)?.let { name ->
                external[it.id()] = name
            }
        }
    }
}