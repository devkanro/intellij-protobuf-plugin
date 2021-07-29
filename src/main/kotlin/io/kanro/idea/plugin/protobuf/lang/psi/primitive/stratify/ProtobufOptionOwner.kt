package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufOptionOwner : ProtobufElement {
    fun options(): Array<ProtobufOptionHover> {
        return if (this is ProtobufBodyOwner) {
            body()?.findChildren() ?: arrayOf()
        } else {
            findChildren()
        }
    }

    /**
     * Find extension options by QualifiedName
     */
    fun options(name: QualifiedName): Array<ProtobufOptionHover> {
        val target = if (this is ProtobufBodyOwner) {
            body() ?: return arrayOf()
        } else {
            this
        }

        return target.findChildren {
            it.isOption(name)
        }
    }

    /**
     * Find builtin options by QualifiedName
     */
    fun options(name: String): Array<ProtobufOptionHover> {
        val target = if (this is ProtobufBodyOwner) {
            body() ?: return arrayOf()
        } else {
            this
        }

        return target.findChildren {
            it.isOption(name)
        }
    }
}
