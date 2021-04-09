package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.psi.impl.source.PsiFileWithStubSupport
import com.intellij.psi.tree.IFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufScope

interface ProtobufFile : PsiFileWithStubSupport, ProtobufScope, ProtobufOptionOwner {
    fun messages(): Iterable<ProtobufMessageDefinition>

    fun imports(): Iterable<ProtobufImportStatement>

    fun enums(): Iterable<ProtobufEnumDefinition>

    fun services(): Iterable<ProtobufServiceDefinition>

    fun packageParts(): Array<ProtobufPackageName>

    fun syntax(): String?

    object Type : IFileElementType("PROTO_FILE", ProtobufLanguage)
}
