package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubExternalProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope

interface ProtobufFile :
    PsiFile,
    ProtobufNamedElement,
    ProtobufScope,
    ProtobufLookupItem,
    ProtobufOptionOwner,
    ItemPresentation {
    fun messages(): Iterable<ProtobufMessageDefinition>

    fun imports(): Iterable<ProtobufImportStatement>

    fun enums(): Iterable<ProtobufEnumDefinition>

    fun services(): Iterable<ProtobufServiceDefinition>

    fun packageParts(): Array<ProtobufPackageName>

    fun resourceDefinitions(): Array<ProtobufOptionHover>

    fun syntax(): String?

    fun addImport(protobufElement: ProtobufElement): Boolean

    fun addImport(path: String): Boolean

    fun stubData(): Array<String> {
        return arrayOf()
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
