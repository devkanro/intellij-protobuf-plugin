package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufStubExternalProvider
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope

interface ProtobufFile :
    PsiFile,
    NamedElement,
    ProtobufScope,
    LookupableElement,
    ProtobufOptionOwner,
    ItemPresentation {
    fun messages(): Iterable<ProtobufMessageDefinition>

    fun imports(): Iterable<ProtobufImportStatement>

    fun enums(): Iterable<ProtobufEnumDefinition>

    fun services(): Iterable<ProtobufServiceDefinition>

    fun packageParts(): Array<ProtobufPackageName>

    fun resourceDefinitions(): Array<ProtobufOptionHover>

    fun syntax(): String?

    fun edition(): String?

    fun addImport(protobufElement: ProtobufElement): Boolean

    fun addImport(path: String): Boolean

    fun stubData(): Array<String> {
        return ArrayUtilRt.EMPTY_STRING_ARRAY
    }

    fun stubExternalData(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        ProtobufStubExternalProvider.extensionPoint.extensionList.forEach {
            it.mergeExternalData(this, result)
        }
        return result
    }

    object Type : IFileElementType("PROTO_FILE", ProtobufLanguage)
}
