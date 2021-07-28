package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufStubSupport<TStub : StubElement<TPsi>, TPsi : ProtobufElement> : StubBasedPsiElement<TStub> {
    fun stubData(): Array<String> {
        return arrayOf()
    }

    fun stubExternalData(): Map<String, String> {
        if (this !is ProtobufNamedElement) return mapOf()

        val result = mutableMapOf<String, String>()
        ProtobufStubExternalProvider.extensionPoint.extensionList.forEach {
            it.mergeExternalData(this, result)
        }
        return result
    }
}
