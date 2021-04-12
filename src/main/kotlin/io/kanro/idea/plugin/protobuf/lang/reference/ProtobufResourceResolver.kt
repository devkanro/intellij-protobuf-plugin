package io.kanro.idea.plugin.protobuf.lang.reference

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.public
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren
import io.kanro.idea.plugin.protobuf.lang.support.Resources
import java.util.Stack

object ProtobufResourceResolver {
    fun resolveAbsolutely(
        file: ProtobufFile,
        resourceName: String
    ): ProtobufElement? {
        resolveAbsolutelyInFile(file, resourceName)?.let { return it }

        val stack = Stack<ProtobufFile>()
        stack.addAll(file.imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val targetFile = stack.pop()
            resolveAbsolutelyInFile(targetFile, resourceName)?.let { return it }
            targetFile.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }
        return null
    }

    fun resolveAbsolutelyInFile(
        file: ProtobufFile,
        resourceName: String
    ): ProtobufElement? {
        file.resourceDefinitions().forEach {
            if (it.value(Resources.resourceTypeField)?.stringValue?.value() == resourceName) {
                return it
            }
        }
        file.walkChildren<ProtobufMessageDefinition> {
            if (it.resourceName() == resourceName) {
                return it
            }
        }
        return null
    }
}
