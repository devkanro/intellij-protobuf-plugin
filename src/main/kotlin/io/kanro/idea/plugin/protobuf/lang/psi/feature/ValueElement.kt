package io.kanro.idea.plugin.protobuf.lang.psi.feature

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement

interface ValueElement<T> : BaseElement {
    fun value(): T

    fun valueType(): ValueType
}

enum class ValueType {
    UNKNOWN,
    STRING,
    NUMBER,
    BOOLEAN,
    ENUM,
    MESSAGE,
    LIST,
}
