package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.token.ProtobufTokens

interface StringValue : ValueElement<String> {
    override fun value(): String =
        buildString {
            var child = firstChild
            while (child != null) {
                if (child.elementType == ProtobufTokens.STRING_LITERAL) {
                    append(decodeStringFromStringLiteral(child))
                }
                child = child.nextSibling
            }
        }

    override fun valueType(): ValueType = ValueType.STRING
}
