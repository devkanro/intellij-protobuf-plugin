package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface BooleanValue : ValueElement<Boolean> {
    override fun value(): Boolean = this.text.toBoolean()
}
