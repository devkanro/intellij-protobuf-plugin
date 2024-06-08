package io.kanro.idea.plugin.protobuf.lang.psi.text

fun ProtoTextTypeName.scope(): ProtoTextElement {
    return root().parent as ProtoTextElement
}
