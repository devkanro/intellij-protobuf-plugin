package io.kanro.idea.plugin.protobuf.aip.reference

import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.public
import io.kanro.idea.plugin.protobuf.lang.psi.proto.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren
import java.util.Stack

object AipResourceResolver {
    fun resolveAbsolutely(
        file: ProtobufFile,
        resourceName: String,
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
        resourceName: String,
    ): ProtobufElement? {
        file.resourceDefinitions().forEach {
            if (it.value(AipOptions.resourceTypeField)?.toString() == resourceName) {
                return it
            }
        }
        file.walkChildren<ProtobufMessageDefinition> {
            if (it.resourceType() == resourceName) {
                return it
            }
        }
        return null
    }

    fun collectAbsolutely(
        file: ProtobufFile,
        result: MutableList<ProtobufElement> = mutableListOf(),
    ): List<ProtobufElement> {
        collectAbsolutelyInFile(file, result)

        val stack = Stack<ProtobufFile>()
        stack.addAll(file.imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val targetFile = stack.pop()
            collectAbsolutelyInFile(targetFile, result)
            targetFile.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }
        return result
    }

    fun collectAbsolutelyInFile(
        file: ProtobufFile,
        result: MutableList<ProtobufElement> = mutableListOf(),
    ): ProtobufElement? {
        file.resourceDefinitions().forEach {
            if (it.value(AipOptions.resourceTypeField)?.toString() != null) {
                result += it
            }
        }
        file.walkChildren<ProtobufMessageDefinition> {
            if (it.resourceType() != null) {
                result += it
            }
        }
        return null
    }
}
