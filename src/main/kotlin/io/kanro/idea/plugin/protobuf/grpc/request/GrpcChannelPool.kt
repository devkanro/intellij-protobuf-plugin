package io.kanro.idea.plugin.protobuf.grpc.request

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import io.grpc.Channel
import io.grpc.HttpConnectProxiedSocketAddress
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service(Service.Level.APP)
class GrpcChannelPool : Disposable {
    private val cache = ConcurrentHashMap<String, ManagedChannel>()

    fun getOrCreateChannel(grpcRequest: GrpcRequest): Channel {
        val id = "http${if (grpcRequest.tls) "s" else ""}://${grpcRequest.host}"
        return cache.getOrPut(id) {
            ManagedChannelBuilder.forTarget(grpcRequest.host).proxyDetector {
                HttpConnectProxiedSocketAddress.newBuilder()
                    .setTargetAddress(it as InetSocketAddress)
                    .setProxyAddress(
                        InetSocketAddress(
                            "127.0.0.1",
                            8887
                        )
                    ).build()
            }.apply {
                if (!grpcRequest.tls) {
                    this.usePlaintext()
                } else {
                    this.useTransportSecurity()
                }
            }.build()
        }
    }

    override fun dispose() {
        cache.forEach { (_, channel) ->
            if (!channel.isShutdown) channel.shutdown()

            if (!channel.isTerminated) channel.awaitTermination(200, TimeUnit.MILLISECONDS)

            if (!channel.isTerminated) channel.shutdownNow()
        }
    }
}