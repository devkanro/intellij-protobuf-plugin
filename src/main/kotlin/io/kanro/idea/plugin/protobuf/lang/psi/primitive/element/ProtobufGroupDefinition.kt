package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.openapi.util.text.StringUtil
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import javax.swing.Icon

interface ProtobufGroupDefinition : ProtobufFieldLike, ProtobufNumberScope, ProtobufMultiNameDefinition {
    @JvmDefault
    override fun type(): String {
        return "group"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.GROUP_FIELD
    }

    @JvmDefault
    override fun fieldType(): String? {
        return identifier()?.text
    }

    @JvmDefault
    override fun name(): String? {
        return identifier()?.text?.let { StringUtil.wordsToBeginFromLowerCase(it) }
    }

    @JvmDefault
    override fun names(): Set<String> {
        return setOfNotNull(name(), identifier()?.text)
    }
}
