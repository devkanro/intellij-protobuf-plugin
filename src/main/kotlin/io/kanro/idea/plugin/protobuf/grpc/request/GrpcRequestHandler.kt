package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.jackson.Json
import com.bybutter.sisyphus.jackson.toJson
import com.bybutter.sisyphus.protobuf.InternalProtoApi
import com.bybutter.sisyphus.protobuf.LocalProtoReflection
import com.bybutter.sisyphus.protobuf.findMessageSupport
import com.bybutter.sisyphus.protobuf.invoke
import com.bybutter.sisyphus.protobuf.jackson.JacksonReader
import com.bybutter.sisyphus.protobuf.jackson.ProtoModule
import com.intellij.httpClient.execution.common.CommonClientResponse
import com.intellij.httpClient.execution.common.CommonClientResponseBody
import com.intellij.httpClient.execution.common.RequestHandler
import com.intellij.httpClient.execution.common.RunContext
import com.intellij.openapi.application.ApplicationManager
import io.grpc.CallOptions
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import io.kanro.idea.plugin.protobuf.grpc.referece.GrpcMethodReference
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Suppress("UnstableApiUsage")
object GrpcRequestHandler : RequestHandler<GrpcRequest> {
    private fun buildProtoTypes(runContext: RunContext): LocalProtoReflection {
        val httpRequest = runContext.requestInfo.requestPointer.element ?: throw IllegalStateException()
        val method =
            httpRequest.requestTarget?.references?.filterIsInstance<GrpcMethodReference>()?.firstOrNull()
                ?.resolve() as? ProtobufRpcDefinition ?: throw IllegalStateException()
        return ProtoFileReflection(method)
    }

    @OptIn(InternalProtoApi::class)
    override fun execute(
        request: GrpcRequest,
        runContext: RunContext,
    ): CommonClientResponse {
        if (ProtoModule::class.java.canonicalName !in Json.mapper.registeredModuleIds) {
            Json.mapper.registerModule(ProtoModule())
        }
        val types =
            ApplicationManager.getApplication().runReadAction<LocalProtoReflection> {
                buildProtoTypes(runContext)
            }
        val inputSupport = types.findMessageSupport(request.inputType)
        val outputSupport = types.findMessageSupport(request.outputType)
        val requests =
            types.invoke {
                request.requests.map {
                    val reader = JacksonReader(Json.mapper.createParser(it))
                    reader.next()
                    inputSupport.newMutable().apply {
                        readFrom(reader)
                    }
                }
            }

        val pool = ApplicationManager.getApplication().getService(GrpcChannelPool::class.java)
        val channel = pool.getOrCreateChannel(request)

        val methodType =
            when {
                request.inputStreaming && request.outputStreaming -> MethodDescriptor.MethodType.BIDI_STREAMING
                request.inputStreaming -> MethodDescriptor.MethodType.CLIENT_STREAMING
                request.outputStreaming -> MethodDescriptor.MethodType.SERVER_STREAMING
                else -> MethodDescriptor.MethodType.UNARY
            }

        val call =
            channel.newCall(
                MethodDescriptor.newBuilder(ByteArrayMarshaller, ByteArrayMarshaller).setFullMethodName(request.method)
                    .setType(methodType).build(),
                CallOptions.DEFAULT,
            )

        val flow =
            MutableSharedFlow<CommonClientResponseBody.TextStream.Message>(
                replay = 5,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
        val lock = ReentrantLock()
        val condition = lock.newCondition()

        val response =
            GrpcResponse(CommonClientResponseBody.TextStream(flow, jsonBodyFileHint("grpc")), null, null, null, 0)

        val start = System.currentTimeMillis()
        call.start(
            object : ClientCall.Listener<ByteArray>() {
                override fun onMessage(message: ByteArray) {
                    val result =
                        types.invoke {
                            outputSupport.parse(message).toJson()
                        }
                    flow.tryEmit(
                        CommonClientResponseBody.TextStream.Message.Content.Chunk(
                            Json.mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(Json.mapper.readTree(result)) + "\n\n",
                        ),
                    )
                    call.request(1)
                }

                override fun onHeaders(headers: Metadata?) {
                    response.header = headers
                }

                override fun onClose(
                    status: Status,
                    trailers: Metadata,
                ) {
                    response.trailer = trailers
                    response.status = status

                    val detail = trailers[GrpcStatusMarshaller.KEY]

                    if (status.isOk) {
                        flow.tryEmit(CommonClientResponseBody.TextStream.Message.ConnectionClosed.End)
                    } else {
                        flow.tryEmit(
                            CommonClientResponseBody.TextStream.Message.ConnectionClosed.WithError(
                                GrpcStatusException(status, detail?.details),
                            ),
                        )
                    }

                    response.executionTime = System.currentTimeMillis() - start
                    lock.withLock {
                        condition.signal()
                    }
                }
            },
            request.metadata,
        )

        requests.forEach {
            call.sendMessage(it.toProto())
        }
        call.halfClose()
        call.request(1)

        lock.withLock {
            // We wait here for 1 second to collect gRPC response header and trailer
            // When a request executing over 1 second, the header and trailer may not visible
            condition.await(1, TimeUnit.SECONDS)
        }

        return response
    }

    override fun prepareExecutionEnvironment(
        request: GrpcRequest,
        runContext: RunContext,
    ) {
    }
}
