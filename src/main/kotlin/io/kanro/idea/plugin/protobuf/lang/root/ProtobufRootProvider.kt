package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement

interface ProtobufRootProvider {
    companion object {
        var extensionPoint: ExtensionPointName<ProtobufRootProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.rootProvider")
    }

    fun id(): String?

    fun getProtobufRoots(context: PsiElement): List<ProtobufRoot>

    fun modificationTracker(context: PsiElement): ModificationTracker
}
