package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement

inline fun <reified T : PsiElement> PsiElement.findChild(): T? {
    var child: PsiElement? = this.firstChild ?: return null
    while (child != null) {
        if (child is T) return child
        child = child.nextSibling
    }
    return return null
}

inline fun <reified T : PsiElement> PsiElement.findChildren(): Array<T> {
    var child: PsiElement? = this.firstChild ?: return arrayOf()
    val result = mutableListOf<T>()
    while (child != null) {
        if (child is T) result += child
        child = child.nextSibling
    }
    return result.toTypedArray()
}

fun ProtobufImportStatement.public(): Boolean {
    return importLabel?.textMatches("public") == true
}

fun ProtobufImportStatement.weak(): Boolean {
    return importLabel?.textMatches("weak") == true
}

fun ProtobufImportStatement.resolve(): ProtobufFile? {
    return reference?.resolve() as? ProtobufFile
}

fun ProtobufOptionName.field(): ProtobufDefinition? {
    this.fieldName?.let {
        return it.reference?.resolve() as? ProtobufDefinition
    }
    this.extensionOptionName?.let {
        return it.typeName?.resolve() as? ProtobufDefinition
    }
    this.builtInOptionName?.let {
        return it.reference?.resolve() as? ProtobufDefinition
    }
    return null
}

fun ProtobufFieldName.message(): ProtobufScope? {
    val field = when (val parent = this.parent) {
        is ProtobufOptionName -> parent.extensionOptionName?.typeName?.resolve()
        is ProtobufFieldAssign -> {
            val messageValue = parent.parent as? ProtobufMessageValue ?: return null
            when (val assign = messageValue.parent) {
                is ProtobufOptionAssign -> {
                    assign.optionName.field()
                }
                is ProtobufFieldAssign -> {
                    assign.fieldName.reference?.resolve() as? ProtobufDefinition
                }
                else -> null
            }
        }
        else -> null
    } ?: return null

    return when (field) {
        is ProtobufGroupField -> field
        is ProtobufFieldDefinition -> field.typeName.resolve() as? ProtobufMessageDefinition
        else -> null
    }
}

fun ProtobufBuiltInOptionName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufTypeName.absolutely(): Boolean {
    return firstChild !is ProtobufSymbolName
}

fun ProtobufTypeName.resolve(): PsiElement? {
    val (_, element) = tryResolve()
    return element
}

fun ProtobufTypeName.lastResolvedPartIndex(): Int {
    val (name, _) = tryResolve()
    name ?: return -1
    return name.componentCount - 1
}

private fun ProtobufTypeName.tryResolve(): Pair<QualifiedName?, PsiElement?> {
    return CachedValuesManager.getCachedValue(this) {
        val filter = when (parent) {
            is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionName(parentOfType())
            is ProtobufFieldDefinition,
            is ProtobufMapField -> ProtobufSymbolFilters.fieldTypeName
            is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeName
            is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeName
            else -> AnyElement
        }
        var name = QualifiedName.fromComponents(this.symbolNameList.map { it.text })
        while (name.componentCount > 0) {
            val result = if (absolutely()) {
                ProtobufSymbolResolver.resolveAbsolutely(this, name, filter)
            } else {
                ProtobufSymbolResolver.resolveRelatively(this, name, filter)
            }
            if (result != null) {
                return@getCachedValue CachedValueProvider.Result.create(
                    name to result,
                    this
                )
            }
            name = name.removeLastComponent()
        }
        CachedValueProvider.Result.create(null to null, PsiModificationTracker.MODIFICATION_COUNT)
    }
}

fun ProtobufEnumValue.enum(): ProtobufEnumDefinition? {
    val field = when (val parent = this.parent) {
        is ProtobufOptionAssign -> {
            parent.optionName.field() as? ProtobufFieldDefinition
        }
        is ProtobufFieldAssign -> {
            parent.fieldName.reference?.resolve() as? ProtobufFieldDefinition
        }
        else -> null
    } ?: return null
    return field.typeName.resolve() as? ProtobufEnumDefinition
}
