package io.kanro.idea.plugin.protobuf.compile.basic

import com.bybutter.sisyphus.protobuf.primitives.DescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.EnumDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.ServiceDescriptorProto
import io.kanro.idea.plugin.protobuf.compile.BaseProtobufCompilerPlugin
import io.kanro.idea.plugin.protobuf.compile.CompileContext
import io.kanro.idea.plugin.protobuf.compile.EnumCompilingState
import io.kanro.idea.plugin.protobuf.compile.FileCompilingState
import io.kanro.idea.plugin.protobuf.compile.MessageCompilingState
import io.kanro.idea.plugin.protobuf.compile.ServiceCompilingState
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.value

class FileCompiler : BaseProtobufCompilerPlugin<FileCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: FileCompilingState,
    ) {
        val file = state.element()
        state.target().apply {
            this.name = file.importPath() ?: throw IllegalStateException("Invalid file: name missing.")
            this.syntax = file.syntax() ?: "proto2"
            this.`package` = file.packageParts().joinToString(".") { it.text }
            this.dependency += file.imports().mapNotNull { it.stringValue?.value() }

            file.items().forEach {
                when (it) {
                    is ProtobufMessageDefinition -> {
                        this.messageType +=
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
                    is ProtobufServiceDefinition -> {
                        this.service +=
                            try {
                                ServiceDescriptorProto {
                                    context.advance(ServiceCompilingState(state, this, it))
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
