package io.kanro.idea.plugin.protobuf.lang.psi.value

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface EnumValue : ValueElement<String> {
    override fun value(): String = this.text
}
