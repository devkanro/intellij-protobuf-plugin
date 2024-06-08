package io.kanro.idea.plugin.protobuf.compile.basic

import com.bybutter.sisyphus.protobuf.primitives.DescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.EnumDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.FieldDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.MessageOptions
import com.bybutter.sisyphus.protobuf.primitives.OneofDescriptorProto
import io.kanro.idea.plugin.protobuf.compile.BaseProtobufCompilerPlugin
import io.kanro.idea.plugin.protobuf.compile.CompileContext
import io.kanro.idea.plugin.protobuf.compile.EnumCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageFieldCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageMapEntryCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageMapFieldCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageOneofCompilingState
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.key
import io.kanro.idea.plugin.protobuf.lang.psi.proto.optional
import io.kanro.idea.plugin.protobuf.lang.psi.proto.repeated
import io.kanro.idea.plugin.protobuf.lang.psi.proto.required
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType
import io.kanro.idea.plugin.protobuf.string.toPascalCase

class MessageCompiler : BaseProtobufCompilerPlugin<MessageCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: MessageCompilingState,
    ) {
        val message = state.element()
        state.target().apply {
            this.name = message.name() ?: throw IllegalStateException("Invalid message definition: name missing.")

            message.items().forEach {
                when (it) {
                    is ProtobufMessageDefinition -> {
                        this.nestedType +=
                            try {
                                DescriptorProto {
                                    context.advance(MessageCompilingState(state, this, it))
                                }
                            } catch (e: Exception) {
                                return@forEach
                            }
                    }

                    is ProtobufEnumDefinition -> {
                        this.enumType +=
                            try {
                                EnumDescriptorProto {
                                    context.advance(EnumCompilingState(state, this, it))
                                }
                            } catch (e: Exception) {
                                return@forEach
                            }
                    }

                    is ProtobufFieldDefinition -> {
                        this.field +=
                            try {
                                FieldDescriptorProto {
                                    context.advance(MessageFieldCompilingState(state, this, it))
                                }
                            } catch (e: Exception) {
                                return@forEach
                            }
                    }

                    is ProtobufOneofDefinition -> {
                        this.oneofDecl +=
                            try {
                                OneofDescriptorProto {
                                    context.advance(MessageOneofCompilingState(state, this, it))
                                }
                            } catch (e: Exception) {
                                return@forEach
                            }
                    }

                    is ProtobufMapFieldDefinition -> {
                        this.nestedType +=
                            try {
                                DescriptorProto {
                                    context.advance(MessageMapEntryCompilingState(state, this, it))
                                }
                            } catch (e: Exception) {
                                return@forEach
                            }
                    }
                }
            }
        }
    }
}

class MessageFieldCompiler : BaseProtobufCompilerPlugin<MessageFieldCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: MessageFieldCompilingState,
    ) {
        val field = state.element()
        val name = field.name() ?: throw IllegalStateException("Invalid field definition: name missing.")
        val number = field.number() ?: throw IllegalStateException("Invalid field definition: number missing.")
        val builtInType = BuiltInType.of(field.typeName.text)

        val parent = state.parent()
        val oneof =
            if (parent is MessageOneofCompilingState) {
                parent.parent().target().oneofDecl.size
            } else {
                -1
            }

        state.target().apply {
            this.name = name
            this.number = number.toInt()
            this.jsonName = field.jsonName() ?: name
            if (oneof >= 0) {
                this.oneofIndex = oneof
            }

            if (builtInType != null) {
                this.type = builtInType.toFieldType()
            } else {
                val type =
                    field.typeName.reference?.resolve()
                        ?: throw IllegalStateException("Invalid field definition: unresolvable field type.")
                when (type) {
                    is ProtobufMessageDefinition -> {
                        this.type = FieldDescriptorProto.Type.MESSAGE
                        this.typeName = type.qualifiedName()?.let { ".$it" }
                            ?: throw IllegalStateException("Invalid field definition: invalid field type.")
                    }

                    is ProtobufEnumDefinition -> {
                        this.type = FieldDescriptorProto.Type.ENUM
                        this.typeName = type.qualifiedName()?.let { ".$it" }
                            ?: throw IllegalStateException("Invalid field definition: invalid field type.")
                    }
                }
            }

            this.label =
                if (field.repeated()) {
                    FieldDescriptorProto.Label.REPEATED
                } else if (field.required()) {
                    FieldDescriptorProto.Label.REQUIRED
                } else if (field.optional()) {
                    if (builtInType != null && field.file().syntax() == "proto3") {
                        this.proto3Optional = true
                    }
                    FieldDescriptorProto.Label.OPTIONAL
                } else {
                    FieldDescriptorProto.Label.OPTIONAL
                }
        }
    }
}

class MessageOneofCompiler : BaseProtobufCompilerPlugin<MessageOneofCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: MessageOneofCompilingState,
    ) {
        val oneof = state.element()
        state.target().apply {
            this.name = oneof.name() ?: throw IllegalStateException("Invalid oneof definition: name missing.")
            state.parent().target().field +=
                oneof.items().mapNotNull {
                    if (it !is ProtobufFieldDefinition) return@mapNotNull null
                    try {
                        FieldDescriptorProto {
                            context.advance(MessageFieldCompilingState(state, this, it))
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
        }
    }
}

class MessageMapEntryCompiler : BaseProtobufCompilerPlugin<MessageMapEntryCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: MessageMapEntryCompilingState,
    ) {
        val field = state.element()
        val name = field.name() ?: throw IllegalStateException("Invalid map field definition: name missing.")

        state.target().apply {
            this.name = "${name}Entry".toPascalCase()
            this.options =
                MessageOptions {
                    mapEntry = true
                }
            val key = field.key() ?: throw IllegalStateException("Invalid map field definition: invalide key type.")
            val value = field.key() ?: throw IllegalStateException("Invalid map field definition: invalide value type.")

            this.field +=
                FieldDescriptorProto {
                    this.name = "key"
                    this.number = 1
                    val builtInType = BuiltInType.of(key.text)
                    this.type = builtInType?.toFieldType() ?: FieldDescriptorProto.Type.ENUM
                    if (this.type == FieldDescriptorProto.Type.ENUM) {
                        this.typeName =
                            (key.reference?.resolve() as? ProtobufEnumDefinition)?.qualifiedName()?.let { ".$it" }
                                ?: throw IllegalStateException("Invalid map field definition: unsolvable key type.")
                    }
                }
            this.field +=
                FieldDescriptorProto {
                    this.name = "value"
                    this.number = 2
                    val builtInType = BuiltInType.of(key.text)
                    if (builtInType != null) {
                        this.type = builtInType.toFieldType()
                    } else {
                        val type =
                            value.reference?.resolve()
                                ?: throw IllegalStateException("Invalid field definition: unresolvable value type.")
                        when (type) {
                            is ProtobufMessageDefinition -> {
                                this.type = FieldDescriptorProto.Type.MESSAGE
                                this.typeName = type.qualifiedName()?.let { ".$it" }
                                    ?: throw IllegalStateException("Invalid field definition: unresolvable value type.")
                            }

                            is ProtobufEnumDefinition -> {
                                this.type = FieldDescriptorProto.Type.ENUM
                                this.typeName = type.qualifiedName()?.let { ".$it" }
                                    ?: throw IllegalStateException("Invalid field definition: unresolvable value type.")
                            }

                            else -> throw IllegalStateException("Invalid field definition: invalid value type.")
                        }
                    }
                }
        }

        when (val parent = state.parent()) {
            is MessageCompilingState -> {
                try {
                    parent.target().field +=
                        FieldDescriptorProto {
                            context.advance(MessageMapFieldCompilingState(state, this, state.element()))
                        }
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }
}

class MessageMapFieldCompiler : BaseProtobufCompilerPlugin<MessageMapFieldCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: MessageMapFieldCompilingState,
    ) {
        val field = state.element()
        val name = field.name() ?: throw IllegalStateException("Invalid map field definition: name missing.")
        val number = field.number() ?: throw IllegalStateException("Invalid field definition: number missing.")
        val entryName = "${name}Entry".toPascalCase()

        val root = state.parent().parent()
        val typename =
            when (root) {
                is MessageCompilingState -> {
                    root.element().qualifiedName()?.let { ".$it.$entryName" }
                        ?: throw IllegalStateException("Invalid map field definition: name missing.")
                }

                else -> throw IllegalStateException("Invalid map field definition: unsupported extension map field.")
            }

        state.target().apply {
            this.name = name
            this.type = FieldDescriptorProto.Type.MESSAGE
            this.label = FieldDescriptorProto.Label.REPEATED
            this.typeName = typename
            this.number = number.toInt()
            this.jsonName = field.jsonName() ?: name
        }
    }
}

private fun BuiltInType.toFieldType(): FieldDescriptorProto.Type {
    return FieldDescriptorProto.Type.valueOf(this.name)
}
