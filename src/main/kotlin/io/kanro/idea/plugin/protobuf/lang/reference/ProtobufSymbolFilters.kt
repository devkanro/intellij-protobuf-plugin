package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendBody
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.support.Options
import io.kanro.idea.plugin.protobuf.lang.util.or

object ProtobufSymbolFilters {
    val packageName = PsiElementFilter {
        it is ProtobufPackageName
    }

    val fieldType = PsiElementFilter {
        it is ProtobufEnumDefinition || it is ProtobufMessageDefinition
    }

    val message = PsiElementFilter {
        it is ProtobufMessageDefinition
    }

    val messageTypeName = message or packageName

    val field = PsiElementFilter {
        it is ProtobufFieldDefinition || it is ProtobufGroupDefinition
    }

    val extensionField = PsiElementFilter {
        (it is ProtobufFieldDefinition || it is ProtobufGroupDefinition) && it.parent is ProtobufExtendBody
    }

    fun extensionField(message: QualifiedName): PsiElementFilter = MessageExtensionFilter(message)

    fun extensionField(option: Options): PsiElementFilter = MessageExtensionFilter(option.qualifiedName)

    val extensionFieldQualifiedName = extensionField or packageName

    fun extensionFieldQualifiedName(message: QualifiedName) = extensionField(message) or packageName

    fun extensionFieldQualifiedName(option: Options) = extensionField(option) or packageName

    val alwaysFalse = PsiElementFilter { false }

    private class MessageExtensionFilter(private val message: QualifiedName) : PsiElementFilter {
        override fun isAccepted(element: PsiElement): Boolean {
            val extend = element.parentOfType<ProtobufExtendDefinition>() ?: return false
            val name =
                (extend.typeName?.reference?.resolve() as? ProtobufMessageDefinition)?.qualifiedName() ?: return false
            return name == message
        }
    }
}
