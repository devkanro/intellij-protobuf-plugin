package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufVirtualScope
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import javax.swing.Icon

interface ProtobufExtendDefinition : ProtobufVirtualScope {
    @JvmDefault
    override fun type(): String {
        return "extend"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.EXTEND
    }

    @JvmDefault
    override fun nameElement(): PsiElement? {
        return findChild<ProtobufTypeName>()
    }

    @JvmDefault
    override fun name(): String? {
        return (findChild<ProtobufTypeName>()?.reference?.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.toString()
    }

    @JvmDefault
    override fun lookup(): LookupElementBuilder? {
        return null
    }
}
