package io.kanro.idea.plugin.protobuf.compile

import com.bybutter.sisyphus.protobuf.MutableMessage
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableEnumDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableEnumValueDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableFieldDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableFileDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableMethodDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableOneofDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.internal.MutableServiceDescriptorProto
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufCompilingState<TDesc : MutableMessage<*, *>, TPsi : ProtobufElement> {
    fun target(): TDesc

    fun element(): TPsi
}

interface ProtobufCompilingSubState<TParent : ProtobufCompilingState<*, *>, TDesc : MutableMessage<*, *>, TPsi : ProtobufElement> :
    ProtobufCompilingState<TDesc, TPsi> {
    fun parent(): TParent
}

class FileCompilingState(private val target: MutableFileDescriptorProto, private val file: ProtobufFile) :
    ProtobufCompilingState<MutableFileDescriptorProto, ProtobufFile> {
    override fun target(): MutableFileDescriptorProto {
        return target
    }

    override fun element(): ProtobufFile {
        return file
    }
}

class MessageCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableDescriptorProto,
    private val message: ProtobufMessageDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableDescriptorProto, ProtobufMessageDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableDescriptorProto {
        return target
    }

    override fun element(): ProtobufMessageDefinition {
        return message
    }
}

class EnumCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableEnumDescriptorProto,
    private val enum: ProtobufEnumDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableEnumDescriptorProto, ProtobufEnumDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableEnumDescriptorProto {
        return target
    }

    override fun element(): ProtobufEnumDefinition {
        return enum
    }
}

class EnumValueCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableEnumValueDescriptorProto,
    private val value: ProtobufEnumValueDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableEnumValueDescriptorProto, ProtobufEnumValueDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableEnumValueDescriptorProto {
        return target
    }

    override fun element(): ProtobufEnumValueDefinition {
        return value
    }
}

class ServiceCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableServiceDescriptorProto,
    private val service: ProtobufServiceDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableServiceDescriptorProto, ProtobufServiceDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableServiceDescriptorProto {
        return target
    }

    override fun element(): ProtobufServiceDefinition {
        return service
    }
}

class ServiceMethodCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableMethodDescriptorProto,
    private val rpc: ProtobufRpcDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableMethodDescriptorProto, ProtobufRpcDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableMethodDescriptorProto {
        return target
    }

    override fun element(): ProtobufRpcDefinition {
        return rpc
    }
}

class MessageFieldCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableFieldDescriptorProto,
    private val field: ProtobufFieldDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableFieldDescriptorProto, ProtobufFieldDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableFieldDescriptorProto {
        return target
    }

    override fun element(): ProtobufFieldDefinition {
        return field
    }
}

class MessageOneofCompilingState(
    private val parent: MessageCompilingState,
    private val target: MutableOneofDescriptorProto,
    private val oneof: ProtobufOneofDefinition
) : ProtobufCompilingSubState<MessageCompilingState, MutableOneofDescriptorProto, ProtobufOneofDefinition> {
    override fun parent(): MessageCompilingState {
        return parent
    }

    override fun target(): MutableOneofDescriptorProto {
        return target
    }

    override fun element(): ProtobufOneofDefinition {
        return oneof
    }
}

class MessageMapEntryCompilingState(
    private val parent: ProtobufCompilingState<*, *>,
    private val target: MutableDescriptorProto,
    private val field: ProtobufMapFieldDefinition
) : ProtobufCompilingSubState<ProtobufCompilingState<*, *>, MutableDescriptorProto, ProtobufMapFieldDefinition> {
    override fun parent(): ProtobufCompilingState<*, *> {
        return parent
    }

    override fun target(): MutableDescriptorProto {
        return target
    }

    override fun element(): ProtobufMapFieldDefinition {
        return field
    }
}

class MessageMapFieldCompilingState(
    private val parent: MessageMapEntryCompilingState,
    private val target: MutableFieldDescriptorProto,
    private val field: ProtobufMapFieldDefinition
) : ProtobufCompilingSubState<MessageMapEntryCompilingState, MutableFieldDescriptorProto, ProtobufMapFieldDefinition> {
    override fun parent(): MessageMapEntryCompilingState {
        return parent
    }

    override fun target(): MutableFieldDescriptorProto {
        return target
    }

    override fun element(): ProtobufMapFieldDefinition {
        return field
    }
}