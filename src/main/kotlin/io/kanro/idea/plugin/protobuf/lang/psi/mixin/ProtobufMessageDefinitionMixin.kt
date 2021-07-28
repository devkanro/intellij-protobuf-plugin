package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.StubBasedProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubSupport
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumValueStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufExtendStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufGroupStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMapFieldStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufOneofStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufPackageNameStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub

abstract class ProtobufMessageDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufMessageStub>,
    ProtobufStubSupport<ProtobufMessageStub, ProtobufMessageDefinition>,
    ProtobufMessageDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufMessageStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "", resourceType() ?: "")
    }
}

abstract class ProtobufFieldDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufFieldStub>,
    ProtobufStubSupport<ProtobufFieldStub, ProtobufFieldDefinition>,
    ProtobufFieldDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufFieldStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufMapFieldDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufMapFieldStub>,
    ProtobufStubSupport<ProtobufMapFieldStub, ProtobufMapFieldDefinition>,
    ProtobufMapFieldDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufMapFieldStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufOneofDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufOneofStub>,
    ProtobufStubSupport<ProtobufOneofStub, ProtobufOneofDefinition>,
    ProtobufOneofDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufOneofStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufGroupDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufGroupStub>,
    ProtobufStubSupport<ProtobufGroupStub, ProtobufGroupDefinition>,
    ProtobufGroupDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufGroupStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(identifier?.text ?: "")
    }
}

abstract class ProtobufEnumDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufEnumStub>,
    ProtobufStubSupport<ProtobufEnumStub, ProtobufEnumDefinition>,
    ProtobufEnumDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufEnumStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufEnumValueDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufEnumValueStub>,
    ProtobufStubSupport<ProtobufEnumValueStub, ProtobufEnumValueDefinition>,
    ProtobufEnumValueDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufEnumValueStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufExtendDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufExtendStub>,
    ProtobufStubSupport<ProtobufExtendStub, ProtobufExtendDefinition>,
    ProtobufExtendDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufExtendStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        val targetType = (typeName?.reference?.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.toString()
        return arrayOf(targetType ?: "")
    }
}

abstract class ProtobufServiceDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufServiceStub>,
    ProtobufStubSupport<ProtobufServiceStub, ProtobufServiceDefinition>,
    ProtobufServiceDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufServiceStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufRpcDefinitionMixin :
    StubBasedProtobufElementBase<ProtobufRpcStub>,
    ProtobufStubSupport<ProtobufRpcStub, ProtobufRpcDefinition>,
    ProtobufRpcDefinition {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufRpcStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}

abstract class ProtobufPackageNameMixin :
    StubBasedProtobufElementBase<ProtobufPackageNameStub>,
    ProtobufStubSupport<ProtobufPackageNameStub, ProtobufPackageName>,
    ProtobufPackageName {
    constructor(node: ASTNode) : super(node)

    constructor(stub: ProtobufPackageNameStub, type: IStubElementType<*, *>) : super(stub, type)

    override fun stubData(): Array<String> {
        return arrayOf(name() ?: "")
    }
}
