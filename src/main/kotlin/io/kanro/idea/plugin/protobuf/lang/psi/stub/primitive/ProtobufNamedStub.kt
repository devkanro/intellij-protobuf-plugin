package io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive

import com.intellij.psi.stubs.Stub
import com.intellij.psi.util.QualifiedName

interface ProtobufScopeItemStub : Stub

interface ProtobufNamedStub : ProtobufScopeItemStub {
    fun name(): String?

    @JvmDefault
    fun qualifiedName(): QualifiedName? {
        val name = name() ?: return null
        return parentOfType<ProtobufScopeStub>()?.scope()?.append(name) ?: QualifiedName.fromComponents(name)
    }
}

interface ProtobufScopeItemContainerStub : Stub {
    @JvmDefault
    fun items(): Array<ProtobufScopeItemStub> {
        return childrenStubs.filterIsInstance<ProtobufScopeItemStub>().toTypedArray()
    }
}

interface ProtobufScopeStub : ProtobufScopeItemContainerStub {
    fun scope(): QualifiedName?
}

interface ProtobufVirtualScopeStub : ProtobufScopeItemContainerStub

inline fun <reified T> Stub.parentOfType(): T? {
    var item: Stub? = this.parentStub
    while (item != null) {
        if (item is T) return item
        item = item.parentStub
    }
    return null
}
