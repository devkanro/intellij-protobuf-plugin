package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive

import com.intellij.psi.stubs.Stub
import com.intellij.psi.util.QualifiedName

interface ProtobufScopeItemStub : Stub {
    fun owner(): ProtobufScopeStub? {
        return parentOfType()
    }
}

interface ProtobufNamedStub : Stub {
    fun name(): String?
}

interface ProtobufDefinitionStub : ProtobufNamedStub, ProtobufScopeItemStub {
    fun qualifiedName(): QualifiedName? {
        val name = name() ?: return null
        return parentOfType<ProtobufScopeStub>()?.scope()?.append(name)
            ?: QualifiedName.fromComponents(name)
    }
}

interface ProtobufFieldLikeStub : ProtobufDefinitionStub

interface ProtobufScopeItemContainerStub : Stub {
    fun items(): Array<ProtobufScopeItemStub> {
        return childrenStubs.filterIsInstance<ProtobufScopeItemStub>().toTypedArray()
    }
}

interface ProtobufScopeStub : ProtobufScopeItemStub, ProtobufScopeItemContainerStub {
    fun scope(): QualifiedName?
}

interface ProtobufVirtualScopeStub : ProtobufScopeItemStub, ProtobufScopeItemContainerStub

inline fun <reified T> Stub.parentOfType(): T? {
    var item: Stub? = this.parentStub
    while (item != null) {
        if (item is T) return item
        item = item.parentStub
    }
    return null
}
