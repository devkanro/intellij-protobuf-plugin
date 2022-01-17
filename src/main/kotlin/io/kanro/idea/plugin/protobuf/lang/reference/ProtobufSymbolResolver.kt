package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.public
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement
import java.util.Stack

object ProtobufSymbolResolver {
    fun resolveAbsolutely(
        element: ProtobufElement,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        resolveAbsolutelyInFile(element.file(), symbol, filter)?.let { return it }

        val stack = Stack<ProtobufFile>()
        stack.addAll(element.file().imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val file = stack.pop()
            val fileResult = resolveAbsolutelyInFile(file, symbol, filter)
            if (fileResult != null) return fileResult

            file.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }
        return null
    }

    fun resolveAbsolutelyInFile(
        file: ProtobufFile,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        val scope = file.scope()
        return if (scope != null) {
            if (!symbol.matchesPrefix(scope)) return null
            resolveInScope(file, symbol.subQualifiedName(scope.componentCount, symbol.componentCount), filter)
        } else {
            resolveInScope(file, symbol, filter)
        }
    }

    fun resolveRelatively(
        element: ProtobufElement,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        val currentFile = element.file()
        val currentScope = currentFile.scope()
        resolveInCurrent(element, symbol, filter)?.let { return it }
        resolveRelativelyInFile(currentFile, currentScope, symbol, filter)?.let { return it }
        val stack = Stack<ProtobufFile>()

        stack.addAll(element.file().imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val file = stack.pop()
            resolveRelativelyInFile(file, currentScope, symbol, filter)?.let { return it }

            file.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }

        return null
    }

    fun resolveInCurrent(
        element: ProtobufElement,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        var scope = element.parentOfType<ProtobufScope>() ?: return null
        while (true) {
            resolveInScope(scope, symbol, filter)?.let { return it }
            scope = scope.parentOfType() ?: return null
        }
    }

    fun resolveRelativelyInFile(
        file: ProtobufFile,
        relativeScope: QualifiedName?,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        val fileScope = file.scope()
        if (fileScope == null || relativeScope == null) {
            return resolveInScope(file, symbol, filter)
        }

        var matchedSize = 0
        while (true) {
            if (matchedSize >= relativeScope.componentCount || matchedSize >= fileScope.componentCount) break
            if (relativeScope.components[matchedSize] != fileScope.components[matchedSize]) break
            matchedSize++
            val realScope = fileScope.subQualifiedName(0, matchedSize).append(symbol)
            if (realScope.matchesPrefix(fileScope)) {
                resolveInScope(
                    file,
                    realScope.subQualifiedName(fileScope.componentCount, realScope.componentCount),
                    filter
                )?.let {
                    return it
                }
            }
        }
        return resolveAbsolutelyInFile(file, symbol, filter)
    }

    fun resolveInScope(
        scope: ProtobufScope,
        symbol: QualifiedName,
        filter: PsiElementFilter = AnyElement
    ): ProtobufElement? {
        scope.items<ProtobufDefinition> {
            if (it.name() == symbol.firstComponent) {
                if (symbol.componentCount == 1) {
                    return it.takeIf { filter.isAccepted(it) }
                }

                if (it is ProtobufScope) {
                    resolveInScope(it, symbol.removeHead(1), filter)?.let { return it }
                }
            }
        }
        return null
    }

    fun collectAbsolute(
        element: ProtobufElement,
        scope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        collectAbsoluteInFile(element.file(), scope, filter, result)

        val stack = Stack<ProtobufFile>()
        stack.addAll(element.file().imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val file = stack.pop()
            collectAbsoluteInFile(file, scope, filter, result)

            file.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }
        return result
    }

    fun collectAbsoluteInFile(
        file: ProtobufFile,
        scope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        val fileScope = file.scope()
        if (fileScope != null) {
            if (!fileScope.matchesPrefix(scope) && !scope.matchesPrefix(fileScope)) return result
            if (fileScope.componentCount > scope.componentCount) {
                val packagePart = file.packageParts()[scope.componentCount]
                if (filter.isAccepted(packagePart)) {
                    result += packagePart
                }
                return result
            }
            collectInScope(
                file,
                scope.subQualifiedName(fileScope.componentCount, scope.componentCount),
                filter,
                result
            )
        } else {
            collectInScope(file, scope, filter, result)
        }
        return result
    }

    fun collectRelatively(
        element: ProtobufElement,
        scope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        val currentFile = element.file()
        if (scope.componentCount == 0) {
            currentFile.packageParts().forEach {
                if (filter.isAccepted(it)) {
                    result += it
                }
            }
        }
        val currentScope = currentFile.scope()
        collectInCurrent(element, scope, filter, result)
        collectRelativelyInFile(currentFile, currentScope, scope, filter, result)
        val stack = Stack<ProtobufFile>()

        stack.addAll(currentFile.imports().mapNotNull { it.resolve() })

        while (stack.isNotEmpty()) {
            val file = stack.pop()
            collectRelativelyInFile(file, currentScope, scope, filter, result)

            file.imports().forEach {
                if (it.public()) {
                    it.resolve()?.let { stack.push(it) }
                }
            }
        }
        return result
    }

    fun collectInCurrent(
        element: ProtobufElement,
        scope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        var parentScope = element.parentOfType<ProtobufScope>() ?: return result
        while (true) {
            collectInScope(parentScope, scope, filter, result)
            parentScope = parentScope.parentOfType() ?: return result
        }
    }

    fun collectRelativelyInFile(
        file: ProtobufFile,
        relativeScope: QualifiedName?,
        scope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        val fileScope = file.scope()
        if (fileScope == null || relativeScope == null) {
            collectInScope(file, scope, filter, result)
            return result
        }

        var matchedSize = 0
        while (true) {
            if (matchedSize >= relativeScope.componentCount || matchedSize >= fileScope.componentCount) break
            if (relativeScope.components[matchedSize] != fileScope.components[matchedSize]) break
            matchedSize++
            val realScope = fileScope.subQualifiedName(0, matchedSize).append(scope)
            collectAbsoluteInFile(file, realScope, filter, result)
        }
        collectAbsoluteInFile(file, scope, filter, result)
        return result
    }

    fun collectInScope(
        scope: ProtobufScope,
        targetScope: QualifiedName,
        filter: PsiElementFilter = AnyElement,
        result: MutableList<ProtobufElement> = mutableListOf()
    ): List<ProtobufElement> {
        if (targetScope.componentCount == 0) {
            scope.items<ProtobufDefinition> {
                if (filter.isAccepted(it)) {
                    result += it
                }
            }
            return result
        }

        scope.items<ProtobufDefinition> {
            if (it.name() == targetScope.firstComponent) {
                if (it is ProtobufScope) {
                    collectInScope(it, targetScope.removeHead(1), filter, result)
                }
            }
        }
        return result
    }
}
