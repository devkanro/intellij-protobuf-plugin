package io.kanro.idea.plugin.protobuf.sisyphus.name

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.jvm.javaPackage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName

object SisyphusMutableMessageNamespace : ExternalProtobufNamespace {
    override fun name(element: ProtobufNamedElement?): String? {
        return when (element) {
            is ProtobufMessageDefinition -> "Mutable${element.name()}"
            else -> null
        }
    }

    override fun name(element: ProtobufNamedStub?): String? {
        return when (element) {
            is ProtobufMessageStub -> "Mutable${element.name()}"
            else -> null
        }
    }

    override fun scope(element: ProtobufScope?): QualifiedName? {
        return when (element) {
            is ProtobufFile -> element.javaPackage()?.toQualifiedName()?.append("internal")
            is ProtobufDefinition -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }

    override fun scope(element: ProtobufScopeStub?): QualifiedName? {
        return when (element) {
            is ProtobufFileStub -> element.externalData("java_package")?.toQualifiedName()?.append("internal")
            is ProtobufDefinitionStub -> scope(element.owner())?.append(name(element))
            else -> null
        }
    }
}