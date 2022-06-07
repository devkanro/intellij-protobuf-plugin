package io.kanro.idea.plugin.protobuf.grpc.request

import io.grpc.MethodDescriptor
import java.io.ByteArrayInputStream
import java.io.InputStream

object ByteArrayMarshaller : MethodDescriptor.Marshaller<ByteArray> {
    override fun stream(p0: ByteArray): InputStream {
        return ByteArrayInputStream(p0)
    }

    override fun parse(p0: InputStream): ByteArray {
        return p0.readAllBytes()
    }
}