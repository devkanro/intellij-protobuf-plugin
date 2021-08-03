package io.kanro.idea.plugin.protobuf.decompile

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import java.util.Stack

object ProtobufDecompiler {
    fun decompile(fileDescriptor: DescriptorProtos.FileDescriptorProto): String {
        return buildProtobuf {
            if (fileDescriptor.hasSyntax()) {
                appendLn("syntax = \"${fileDescriptor.syntax}\";")
            }

            if (fileDescriptor.hasPackage()) {
                normalizeStatementLn()
                appendLn("package ${fileDescriptor.`package`};")
            }

            fileDescriptor.dependencyList.forEachIndexed { index, s ->
                val public = fileDescriptor.publicDependencyList.contains(index)
                val weak = fileDescriptor.weakDependencyList.contains(index)

                if (index == 0) {
                    normalizeStatementLn()
                } else {
                    normalizeLn()
                }

                append("import ")
                if (public) append("public ")
                if (weak) append("weak ")
                append("\"$s\";").ln()
            }

            normalizeStatementLn()
            generateBlockOption(this, fileDescriptor.options)
            normalizeStatementLn()

            val stack = Stack<Message>()
            stack.add(fileDescriptor)

            fileDescriptor.serviceList.forEach {
                generate(this, stack, it)
            }

            fileDescriptor.messageTypeList.forEach {
                generate(this, stack, it)
            }

            fileDescriptor.enumTypeList.forEach {
                generate(this, stack, it)
            }
        }
    }

    fun generate(
        builder: ProtobufCodeBuilder,
        stack: Stack<Message>,
        service: DescriptorProtos.ServiceDescriptorProto
    ): Unit = stackWrapper(stack, service) {
        builder.apply {
            normalizeStatementLn()
            generateBlockOption(this, service.options)
            normalizeStatementLn()
            block("service ${service.name}") {
                service.methodList.forEach {
                    generate(builder, stack, it)
                }
            }
        }
    }

    fun generate(
        builder: ProtobufCodeBuilder,
        stack: Stack<Message>,
        method: DescriptorProtos.MethodDescriptorProto
    ): Unit =
        stackWrapper(stack, method) {
            builder.apply {
                normalizeStatementLn()
                append(
                    "rpc ${method.name}(${stack.simpleTypeName(method.inputType)}) returns (${
                    stack.simpleTypeName(
                        method.outputType
                    )
                    });"
                )
            }
        }

    fun generate(builder: ProtobufCodeBuilder, stack: Stack<Message>, message: DescriptorProtos.DescriptorProto): Unit =
        stackWrapper(stack, message) {
            builder.apply {
                normalizeStatementLn()
                block("message ${message.name}") {
                    normalizeStatementLn()
                    generateBlockOption(this, message.options)
                    normalizeStatementLn()

                    val groups = mutableSetOf<String>()

                    message.fieldList.forEach {
                        if (it.hasOneofIndex()) return@forEach
                        if (it.type == DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP) {
                            groups += it.typeName.substringAfterLast('.')
                        }
                        generate(builder, stack, it)
                    }

                    message.nestedTypeList.forEach {
                        if (it.options.mapEntry) return@forEach
                        if (it.name in groups) return@forEach
                        generate(builder, stack, it)
                    }

                    message.enumTypeList.forEach {
                        generate(this, stack, it)
                    }
                }
            }
        }

    fun generate(
        builder: ProtobufCodeBuilder,
        stack: Stack<Message>,
        field: DescriptorProtos.FieldDescriptorProto
    ): Unit = stackWrapper(stack, field) {
        builder.apply {
            normalizeStatementLn()
            findMapEntry(stack, field)?.let {
                val key = it.fieldList.firstOrNull { it.number == 1 }?.let { stack.fieldType(it) } ?: "unknown"
                val value = it.fieldList.firstOrNull { it.number == 2 }?.let { stack.fieldType(it) } ?: "unknown"
                appendLn("map<$key, $value> ${field.name} = ${field.number};")
                return@apply
            }

            findGroup(stack, field)?.let {
                block("${stack.label(field)}group ${field.name} = ${field.number}") {
                    normalizeStatementLn()
                    generateBlockOption(this, it.options)
                    normalizeStatementLn()
                    it.fieldList.forEach {
                        generate(builder, stack, it)
                    }
                }
                return@apply
            }

            append(stack.label(field))
            append(stack.fieldType(field) ?: "Unknown")
            append(" ")
            append(field.name)
            append(" = ${field.number};").ln()
        }
    }

    fun generate(
        builder: ProtobufCodeBuilder,
        stack: Stack<Message>,
        enum: DescriptorProtos.EnumDescriptorProto
    ): Unit = stackWrapper(stack, enum) {
        builder.apply {
            normalizeStatementLn()
            block("enum ${enum.name}") {
                normalizeStatementLn()
                generateBlockOption(this, enum.options)
                normalizeStatementLn()
                enum.valueList.forEach {
                    generate(builder, stack, it)
                }
            }
        }
    }

    fun generate(
        builder: ProtobufCodeBuilder,
        stack: Stack<Message>,
        enumValue: DescriptorProtos.EnumValueDescriptorProto
    ): Unit = stackWrapper(stack, enumValue) {
        builder.apply {
            normalizeStatementLn()
            appendLn("${enumValue.name} = ${enumValue.number};")
        }
    }

    fun generateBlockOption(
        builder: ProtobufCodeBuilder,
        option: Message
    ) {
        builder.apply {
            option.allFields.forEach { (field, value) ->
                when (field.type) {
                    Descriptors.FieldDescriptor.Type.STRING -> {
                        appendLn("option ${field.name} = \"${value}\";")
                    }
                    Descriptors.FieldDescriptor.Type.ENUM -> {
                        appendLn("option ${field.name} = $value;")
                    }
                    Descriptors.FieldDescriptor.Type.BOOL -> {
                        appendLn("option ${field.name} = $value;")
                    }
                    Descriptors.FieldDescriptor.Type.GROUP,
                    Descriptors.FieldDescriptor.Type.MESSAGE,
                    Descriptors.FieldDescriptor.Type.BYTES -> {}
                    else -> {
                        appendLn("option ${field.name} = $value;")
                    }
                }
            }
        }
    }

    private fun Stack<Message>.fieldType(field: DescriptorProtos.FieldDescriptorProto): String? {
        return when (field.type) {
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM,
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE -> simpleTypeName(field.typeName)
            DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP -> TODO()
            else -> field.type.name.lowercase().removePrefix("type_")
        }
    }

    private fun Stack<Message>.label(field: DescriptorProtos.FieldDescriptorProto): String {
        return when (field.label) {
            DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL -> when (file().syntax) {
                "proto3" -> ""
                else -> "optional "
            }
            DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED -> "required "
            DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED -> "repeated "
            else -> ""
        }
    }

    private fun Stack<Message>.file(): DescriptorProtos.FileDescriptorProto {
        return firstElement() as DescriptorProtos.FileDescriptorProto
    }

    private fun Stack<Message>.simpleTypeName(typeName: String?): String? {
        typeName ?: return null
        return typeName.removePrefix(".").removePrefix("${file().`package`}.")
    }

    private inline fun <reified T : Message> Stack<Message>.parent(): T? {
        for (message in asReversed()) {
            if (message is T) return message
        }
        return null
    }

    private fun findMapEntry(
        stack: Stack<Message>,
        field: DescriptorProtos.FieldDescriptorProto
    ): DescriptorProtos.DescriptorProto? {
        if (field.label != DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) return null
        val entryName = field.typeName.substringAfterLast('.')
        val message = stack.parent<DescriptorProtos.DescriptorProto>() ?: return null
        val type = message.nestedTypeList.firstOrNull {
            it.name == entryName
        } ?: return null
        return if (type.options.mapEntry) type else null
    }

    private fun findGroup(
        stack: Stack<Message>,
        field: DescriptorProtos.FieldDescriptorProto
    ): DescriptorProtos.DescriptorProto? {
        if (field.type != DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP) return null
        val groupLevel = stack.asReversed().indexOfFirst { it is DescriptorProtos.DescriptorProto }
        val qName = field.typeName.toQualifiedName()
        val subName = qName.subQualifiedName(qName.componentCount - groupLevel, qName.componentCount)
        var message = stack.parent<DescriptorProtos.DescriptorProto>() ?: return null
        for (component in subName.components) {
            message = message.nestedTypeList.firstOrNull {
                it.name == component
            } ?: return null
        }
        return message
    }

    private fun stackWrapper(stack: Stack<Message>, item: Message, block: () -> Unit) {
        stack.push(item)
        block()
        if (stack.pop() != item) throw IllegalStateException("Wrong stack")
    }
}
