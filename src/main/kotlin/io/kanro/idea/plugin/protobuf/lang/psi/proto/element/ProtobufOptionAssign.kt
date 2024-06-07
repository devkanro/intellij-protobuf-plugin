package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike

interface ProtobufOptionAssign : ValueAssign {
    override fun field(): NamedElement? {
        return findChild<ProtobufOptionName>()
            ?.optionFieldNameList?.lastOrNull()
            ?.reference?.resolve() as? ProtobufFieldLike
    }
}
