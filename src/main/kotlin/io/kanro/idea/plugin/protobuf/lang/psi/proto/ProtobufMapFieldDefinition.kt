package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufMapFieldDefinition.key(): ProtobufTypeName? {
    if (typeNameList.size < 2) return null
    return typeNameList[0]
}

fun ProtobufMapFieldDefinition.value(): ProtobufTypeName? {
    if (typeNameList.size < 2) return null
    return typeNameList[1]
}
