package io.kanro.idea.plugin.protobuf.compile.basic

import com.bybutter.sisyphus.protobuf.primitives.EnumValueDescriptorProto
import io.kanro.idea.plugin.protobuf.compile.BaseProtobufCompilerPlugin
import io.kanro.idea.plugin.protobuf.compile.CompileContext
import io.kanro.idea.plugin.protobuf.compile.EnumCompilingState
import io.kanro.idea.plugin.protobuf.compile.EnumValueCompilingState
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition

class EnumCompiler : BaseProtobufCompilerPlugin<EnumCompilingState>() {
    override fun compile(context: CompileContext, state: EnumCompilingState) {
        val enum = state.element()
        state.target().apply {
            this.name = enum.name() ?: throw IllegalStateException("Invalid enum definition: name missing.")
            this.value += enum.items().mapNotNull {
                if (it !is ProtobufEnumValueDefinition) return@mapNotNull null
                try {
                    EnumValueDescriptorProto {
                        context.advance(EnumValueCompilingState(state, this, it))
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}

class EnumValueCompiler : BaseProtobufCompilerPlugin<EnumValueCompilingState>() {
    override fun compile(context: CompileContext, state: EnumValueCompilingState) {
        val value = state.element()
        state.target().apply {
            this.name = value.name() ?: throw IllegalStateException("Invalid enum definition: name missing.")
            this.number =
                value.number()?.toInt() ?: throw IllegalStateException("Invalid enum definition: number missing.")
        }
    }
}