package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentOwner
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufVirtualScope
import javax.swing.Icon

interface ProtobufExtendDefinition :
    ProtobufVirtualScope,
    ProtobufDefinition,
    DocumentOwner {
    override fun type(): String {
        return "extend"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.EXTEND
    }

    override fun nameElement(): PsiElement? {
        return findChild<ProtobufTypeName>()
    }

    override fun name(): String? {
        return (findChild<ProtobufTypeName>()?.reference?.resolve() as? ProtobufMessageDefinition)?.qualifiedName()
            ?.toString()
    }

    override fun lookup(name: String?): LookupElementBuilder? {
        return null
    }
}
