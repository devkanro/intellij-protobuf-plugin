package io.kanro.idea.plugin.protobuf.lang.psi.primitive

interface ProtobufNumberScope : ProtobufElement {
    fun numbered(): Array<ProtobufNumbered>

    fun reservedRange(): Array<ProtobufReservedNumber>
}
