package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufNumbered
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufReservedNumber

open class NumberTracker(scope: ProtobufNumberScope) {
    private val numberMap = mutableMapOf<Long, MutableList<ProtobufNumbered>>()
    private val reservedNameMap = mutableMapOf<LongRange, ProtobufReservedNumber>()

    init {
        scope.numbered().forEach { record(it) }
        scope.reservedRange().forEach { record(it) }
    }

    protected open fun record(numbered: ProtobufNumbered) {
        val number = numbered.number() ?: return
        numberMap.getOrPut(number) {
            mutableListOf()
        }.add(numbered)
    }

    protected open fun record(reserved: ProtobufReservedNumber) {
        val range = reserved.range() ?: return
        reservedNameMap[range] = reserved
    }

    open fun visit(numbered: ProtobufNumbered, holder: AnnotationHolder) {
        val number = numbered.number() ?: return
        createError(numbered, buildMessage(number, numbered) ?: return, holder)
    }

    open fun visit(reserved: ProtobufReservedNumber, holder: AnnotationHolder) {
        val range = reserved.range() ?: return
        createError(reserved, buildMessage(range, reserved) ?: return, holder)
    }

    protected open fun buildMessage(
        number: Long,
        numbered: ProtobufNumbered
    ): String? {
        reservedNameMap.forEach { (range, element) ->
            if (range.contains(number)) {
                return "Number reserved: \"${element.text}\""
            }
        }
        val elements = numberMap[number] ?: listOf()
        if (elements.size > 1) {
            return "Conflicting declarations: \"$number\""
        }
        return null
    }

    protected open fun buildMessage(
        range: LongRange,
        reserved: ProtobufReservedNumber
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
        ).range(numbered.numberElement()?.textRange ?: numbered.textRange).create()
    }

    protected open fun createError(reserved: ProtobufReservedNumber, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            message
        ).range(reserved.textRange).create()
    }

    companion object {
        fun tracker(scope: ProtobufNumberScope): NumberTracker {
            return CachedValuesManager.getCachedValue(scope) {
                CachedValueProvider.Result.create(
                    NumberTracker(scope), PsiModificationTracker.MODIFICATION_COUNT
                )
            }
        }
    }
}
