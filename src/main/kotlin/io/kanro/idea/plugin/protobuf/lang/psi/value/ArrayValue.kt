package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.util.childrenOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface ArrayValue : ValueElement<Array<Any?>> {
    override fun value(): Array<Any?> =
        this.childrenOfType<ValueElement<*>>().map {
            it.value()
        }.toTypedArray()

    fun value(index: Int): Any? = this.childrenOfType<ValueElement<*>>().getOrNull(index)?.value()
}
