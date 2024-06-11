package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.childrenOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType

interface MessageValue : ValueElement<Any> {
    override fun value(): Any = this

    override fun valueType(): ValueType = ValueType.MESSAGE

    fun value(field: QualifiedName): Any? {
        return valueElement(field)?.value()
    }

    fun valueElement(field: QualifiedName): ValueElement<*>? {
        if (field.componentCount == 0) return this

        val fields = childrenOfType<ValueAssign>()
        fields.forEach {
            if (it.field()?.name() != field.firstComponent) {
                return null
            }
            val next = field.removeHead(1)

            if (next.componentCount == 0) {
                return it
            }

            if (it is MessageValue) {
                return it.valueElement(next)
            }
        }
        return null
    }
}
