package io.kanro.idea.plugin.protobuf.compile.basic

import com.bybutter.sisyphus.protobuf.primitives.MethodDescriptorProto
import io.kanro.idea.plugin.protobuf.compile.BaseProtobufCompilerPlugin
import io.kanro.idea.plugin.protobuf.compile.CompileContext
import io.kanro.idea.plugin.protobuf.compile.ServiceCompilingState
import io.kanro.idea.plugin.protobuf.compile.ServiceMethodCompilingState
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stream

class ServiceCompiler : BaseProtobufCompilerPlugin<ServiceCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: ServiceCompilingState,
    ) {
        val service = state.element()
        state.target().apply {
            this.name = service.name() ?: throw IllegalStateException("Invalid service definition: name missing.")
            this.method +=
                service.items().mapNotNull {
                    if (it !is ProtobufRpcDefinition) return@mapNotNull null
                    try {
                        MethodDescriptorProto {
                            context.advance(ServiceMethodCompilingState(state, this, it))
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
        }
    }
}

class ServiceMethodCompiler : BaseProtobufCompilerPlugin<ServiceMethodCompilingState>() {
    override fun compile(
        context: CompileContext,
        state: ServiceMethodCompilingState,
    ) {
        val rpc = state.element()
        state.target().apply {
            this.name = rpc.name() ?: throw IllegalStateException("Invalid rpc definition: name missing.")
            val input = rpc.input() ?: throw IllegalStateException("Invalid rpc definition: input missing.")
            val output = rpc.output() ?: throw IllegalStateException("Invalid rpc definition: output missing.")
            this.inputType =
                (input.typeName.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.let { ".$it" }
                    ?: throw IllegalStateException("Invalid rpc definition: unresolvable input message.")
            this.outputType =
                (output.typeName.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.let { ".$it" }
                    ?: throw IllegalStateException("Invalid rpc definition: unresolvable output message.")
            this.clientStreaming = input.stream()
            this.serverStreaming = output.stream()
        }
    }
}
