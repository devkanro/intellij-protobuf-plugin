package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.childrenOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement

interface MessageValue : ValueElement<Any> {
    override fun value(): Any = this

    fun value(field: QualifiedName): Any? {
        if (field.componentCount == 0) return value()

        val fields = childrenOfType<ValueAssign>()
        fields.forEach {
            if (it.field()?.name() != field.firstComponent) {
                return null
            }
            val next = field.removeHead(1)
            val value = it.value()

            if (next.componentCount == 0) {
                return value
            }

            if (value is MessageValue) {
                return value.value(next)
            }
        }
        return null
    }
}
