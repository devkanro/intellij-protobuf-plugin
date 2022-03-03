package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.range
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

open class NumberTracker(scope: ProtobufNumberScope, val minValue: Long) {
    private val numberMap = mutableMapOf<Long, MutableList<ProtobufNumbered>>()
    private val reservedNameMap = mutableMapOf<LongRange, ProtobufReservedRange>()
    private val allowAlias = scope.allowAlias()

    init {
        scope.realItems().forEach { record(it) }
        scope.reservedRange().forEach { record(it) }
    }

    protected open fun record(numbered: ProtobufScopeItem) {
        if (numbered !is ProtobufNumbered) return
        val number = numbered.number() ?: return
        numberMap.getOrPut(number) {
            mutableListOf()
        }.add(numbered)
    }

    protected open fun record(reserved: ProtobufReservedRange) {
        val range = reserved.range() ?: return
        reservedNameMap[range] = reserved
    }

    open fun visit(numbered: ProtobufNumbered, holder: AnnotationHolder) {
        val number = numbered.number() ?: return
        createError(numbered, buildMessage(number, numbered) ?: return, holder)
        if (number < minValue) {
        }
    }

    open fun visit(reserved: ProtobufReservedRange, holder: AnnotationHolder) {
        val range = reserved.range() ?: return
        createError(reserved, buildMessage(range, reserved) ?: return, holder)
    }

    protected open fun buildMessage(
        number: Long,
        numbered: ProtobufNumbered
    ): String? {
        if (number < minValue) return "Wrong number, the min value is $minValue"
        reservedNameMap.forEach { (range, element) ->
            if (range.contains(number)) {
                return "Number reserved: \"${element.text}\""
            }
        }
        val elements = numberMap[number] ?: listOf()
        if (elements.isEmpty()) return null
        if (!allowAlias && (elements.size > 1 || elements[0] != numbered)) {
            return "Conflicting declarations: \"$number\""
        }
        return null
    }

    protected open fun buildMessage(
        range: LongRange,
        reserved: ProtobufReservedRange
    ): String? {
        reservedNameMap.forEach { (r, element) ->
            if (element == reserved) return@forEach
            if (r.first in range || r.last in range || range.first in r || range.last in r) {
                return "Conflicting reserved declarations: \"${reserved.text}\", \"${element.text}\""
            }
        }
        return null
    }

    protected open fun createError(numbered: ProtobufNumbered, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            message
        ).range(numbered.intValue()?.textRange ?: numbered.textRange).create()
    }

    protected open fun createError(reserved: ProtobufReservedRange, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            message
        ).range(reserved.textRange).create()
    }

    companion object {
        fun tracker(scope: ProtobufNumberScope): NumberTracker {
            return CachedValuesManager.getCachedValue(scope) {
                val minValue = if (scope is ProtobufMessageDefinition) {
                    1L
                } else 0L
                CachedValueProvider.Result.create(
                    NumberTracker(scope, minValue), PsiModificationTracker.MODIFICATION_COUNT
                )
            }
        }
    }
}
