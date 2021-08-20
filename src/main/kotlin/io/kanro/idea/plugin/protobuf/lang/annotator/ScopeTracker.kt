package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufMultiNameDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem

open class ScopeTracker(scope: ProtobufScope) {
    private val nameMap = mutableMapOf<String, MutableList<ProtobufScopeItem>>()
    private val reservedNameMap = mutableMapOf<String, MutableList<ProtobufReservedName>>()

    init {
        scope.items<ProtobufDefinition> { record(it) }
        scope.reservedNames().forEach { record(it) }
    }

    protected open fun record(definition: ProtobufDefinition) {
        if (definition is ProtobufMultiNameDefinition) {
            definition.names().forEach {
                nameMap.getOrPut(it) {
                    mutableListOf()
                }.add(definition)
            }
        } else {
            val name = definition.name() ?: return
            nameMap.getOrPut(name) {
                mutableListOf()
            }.add(definition)
        }
    }

    protected open fun record(reserved: ProtobufReservedName) {
        val name = reserved.identifierLiteral?.text ?: return
        reservedNameMap.getOrPut(name) {
            mutableListOf()
        }.add(reserved)
    }

    open fun visit(definition: ProtobufDefinition, holder: AnnotationHolder) {
        val name = definition.name() ?: return
        createError(definition, buildMessage(name, definition) ?: return, holder)
    }

    open fun visit(reserved: ProtobufReservedName, holder: AnnotationHolder) {
        val name = reserved.identifierLiteral?.text ?: return
        createError(reserved, buildMessage(name, reserved) ?: return, holder)
    }

    protected open fun buildMessage(
        name: String,
        definition: ProtobufScopeItem
    ): String? {
        val reserves = reservedNameMap[name] ?: listOf()
        if (reserves.isNotEmpty()) {
            return "Name reserved: \"$name\""
        }
        val elements = nameMap[name] ?: listOf()
        if (elements.isEmpty()) return null
        if (elements.size > 1 || elements[0] != definition) {
            return "Conflicting declarations: \"$name\""
        }
        return null
    }

    protected open fun buildMessage(
        name: String,
        reserved: ProtobufReservedName
    ): String? {
        val reserves = reservedNameMap[name] ?: listOf()
        if (reserves.size > 1) {
            return "Conflicting reserved declarations: \"$name\""
        }
        return null
    }

    protected open fun createError(definition: ProtobufDefinition, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            message
        ).range(definition.nameElement()?.textRange ?: definition.textRange).create()
    }

    protected open fun createError(reserved: ProtobufReservedName, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            message
        ).range(reserved.textRange).create()
    }

    companion object {
        fun tracker(scope: ProtobufScope): ScopeTracker {
            return CachedValuesManager.getCachedValue(scope) {
                CachedValueProvider.Result.create(
                    ScopeTracker(scope), PsiModificationTracker.MODIFICATION_COUNT
                )
            }
        }
    }
}
