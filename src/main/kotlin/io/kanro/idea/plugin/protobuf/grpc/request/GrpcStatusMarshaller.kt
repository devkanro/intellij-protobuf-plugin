package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.rpc.Status
import io.grpc.Metadata

object GrpcStatusMarshaller : Metadata.BinaryMarshaller<Status> {
    val KEY = Metadata.Key.of("grpc-status-bin", this)

    override fun toBytes(p0: com.bybutter.sisyphus.rpc.Status): ByteArray {
        return p0.toProto()
    }

    override fun parseBytes(p0: ByteArray): com.bybutter.sisyphus.rpc.Status {
        return com.bybutter.sisyphus.rpc.Status.parse(p0)
    }
}
