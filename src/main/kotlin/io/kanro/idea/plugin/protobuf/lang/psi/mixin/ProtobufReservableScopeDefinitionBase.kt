package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedStatement
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufReservedNumber

abstract class ProtobufReservableScopeDefinitionBase(node: ASTNode) :
    ProtobufNumberedScopeDefinitionBase(node) {

    override fun reservedRange(): Array<ProtobufReservedNumber> {
        val body = body() ?: return arrayOf()
        return body.findChildren<ProtobufReservedStatement>().asSequence()
            .flatMap {
                it.findChildren<ProtobufReservedRange>().asSequence()
            }.toList().toTypedArray()
    }

    override fun reservedNames(): Array<ProtobufReservedName> {
        val body = body() ?: return arrayOf()
        return body.findChildren<ProtobufReservedStatement>().asSequence()
            .flatMap {
                it.findChildren<ProtobufReservedName>().asSequence()
            }.toList().toTypedArray()
    }
}

abstract class ProtobufReservableScopeWithExtensionDefinitionBase(node: ASTNode) :
    ProtobufReservableScopeDefinitionBase(node) {

    override fun reservedRange(): Array<ProtobufReservedNumber> {
        val body = body() ?: return arrayOf()
        return body.findChildren<ProtobufReservedStatement>().asSequence()
            .plus(body.findChildren<ProtobufExtensionStatement>().asSequence())
            .flatMap {
                it.findChildren<ProtobufReservedRange>().asSequence()
            }.toList().toTypedArray()
    }
}
