package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufDocumented
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufVirtualScope
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import javax.swing.Icon

interface ProtobufExtendDefinition :
    ProtobufVirtualScope,
    ProtobufDefinition,
    ProtobufDocumented {
    override fun type(): String {
        return "extend"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.EXTEND
    }

    override fun nameElement(): PsiElement? {
        return findChild<ProtobufTypeName>()
    }

    override fun name(): String? {
        return (findChild<ProtobufTypeName>()?.reference?.resolve() as? ProtobufMessageDefinition)?.qualifiedName()?.toString()
    }

    override fun lookup(): LookupElementBuilder? {
        return null
    }
}
