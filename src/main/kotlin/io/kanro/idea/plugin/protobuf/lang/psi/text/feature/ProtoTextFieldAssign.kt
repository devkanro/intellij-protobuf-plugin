package io.kanro.idea.plugin.protobuf.lang.psi.text.feature

import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextField

interface ProtoTextFieldAssign : ValueAssign {
    override fun field(): NamedElement? {
        val field = findChild<ProtoTextField>() ?: return null
        return field.reference?.resolve() as? NamedElement
    }
}
