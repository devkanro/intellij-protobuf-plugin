package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufImportStatement.public(): Boolean {
    return importLabel?.textMatches("public") == true
}

fun ProtobufImportStatement.weak(): Boolean {
    return importLabel?.textMatches("weak") == true
}

fun ProtobufImportStatement.resolve(): ProtobufFile? {
    return reference?.resolve() as? ProtobufFile
}
