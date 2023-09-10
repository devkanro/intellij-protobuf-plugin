package io.kanro.idea.plugin.protobuf.java

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub

class JavaIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        when (stub) {
            is ProtobufServiceStub -> {
                // impls
                sink.occurrence(JavaNameIndex.key, stub.fullImplBaseName().toString())
                sink.occurrence(JavaNameIndex.key, stub.fullCoroutineImplBaseName().toString())
                // stubs
                sink.occurrence(JavaNameIndex.key, stub.fullStubName().toString())
                sink.occurrence(JavaNameIndex.key, stub.fullBlockingStubName().toString())
                sink.occurrence(JavaNameIndex.key, stub.fullFutureStubName().toString())
                sink.occurrence(JavaNameIndex.key, stub.fullCoroutineStubName().toString())
            }
            is ProtobufRpcStub -> {
                val methodName = stub.methodName()

                // impls
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullImplBaseName()?.append(methodName).toString()
                )
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullCoroutineImplBaseName()?.append(methodName).toString()
                )
                // stubs
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullStubName()?.append(methodName).toString()
                )
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullBlockingStubName()?.append(methodName).toString()
                )
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullFutureStubName()?.append(methodName).toString()
                )
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullCoroutineStubName()?.append(methodName).toString()
                )
            }
        }
    }
}
