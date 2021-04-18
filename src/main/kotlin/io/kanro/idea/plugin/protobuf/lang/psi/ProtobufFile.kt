package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope

interface ProtobufFile : PsiFile, ProtobufScope, ProtobufOptionOwner {
    fun messages(): Iterable<ProtobufMessageDefinition>

    fun imports(): Iterable<ProtobufImportStatement>

    fun enums(): Iterable<ProtobufEnumDefinition>

    fun services(): Iterable<ProtobufServiceDefinition>

    fun packageParts(): Array<ProtobufPackageName>

    fun resourceDefinitions(): Array<ProtobufOptionHover>

    fun syntax(): String?

    fun addImport(protobufElement: ProtobufElement): Boolean

    object Type : IFileElementType("PROTO_FILE", ProtobufLanguage)
}
