package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.util.childrenOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType

interface ArrayValue : ValueElement<Array<Any?>> {
    override fun value(): Array<Any?> = this.childrenOfType<ValueElement<*>>().map {
        it.value()
    }.toTypedArray()

    fun value(index: Int): Any? = valueElement(index)?.value()

    fun values(): Array<ValueElement<*>> = this.childrenOfType<ValueElement<*>>().toTypedArray()

    fun valueElement(index: Int): ValueElement<*>? = this.childrenOfType<ValueElement<*>>().getOrNull(index)

    override fun valueType(): ValueType = ValueType.LIST
}
