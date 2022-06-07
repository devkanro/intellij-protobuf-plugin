package io.kanro.idea.plugin.protobuf.compile

import com.bybutter.sisyphus.protobuf.primitives.FileDescriptorProto
import com.bybutter.sisyphus.protobuf.primitives.FileDescriptorSet
import com.bybutter.sisyphus.reflect.getTypeArgument
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import java.lang.reflect.ParameterizedType
import java.util.Stack

/**
 * Compile protobuf files to [FileDescriptorProto]s, it is written in pure kotlin base on PSI tree.
 * All uncompleted element will be ignored in compiling. Maybe not include   all feature of official protoc compiler.
 */
object Protoc {
    fun compileFiles(elements: List<ProtobufElement>): FileDescriptorSet {
        val stack = Stack<ProtobufFile>()
        val compiledFiles = mutableSetOf<String>()
        stack += elements.map { it.file() }

        return FileDescriptorSet {
            while (stack.isNotEmpty()) {
                val file = stack.pop()
                val fileName = file.importPath() ?: continue
                if (fileName in compiledFiles) continue
                this.file += compileFile(file) ?: continue
                file.imports().forEach {
                    it.resolve()?.let {
                        stack.push(it)
                    }
                }
                compiledFiles += fileName
            }
        }
    }

    fun compileFile(file: ProtobufFile): FileDescriptorProto? {
        return CachedValuesManager.getCachedValue(file) {
            val context = CompileContext(ProtobufCompilerPlugin.extensionPoint.extensionList)

            try {
                CachedValueProvider.Result.create(FileDescriptorProto {
                    context.advance(FileCompilingState(this, file))
                }, PsiModificationTracker.MODIFICATION_COUNT)
            } catch (e: Exception) {
                null
            }
        }
    }
}

interface ProtobufCompilerPlugin<T : ProtobufCompilingState<*, *>> {
    fun accept(state: T): Boolean

    fun compile(context: CompileContext, state: T)

    companion object {
        var extensionPoint: ExtensionPointName<ProtobufCompilerPlugin<in ProtobufCompilingState<*, *>>> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.protocPlugin")
    }
}

class CompileContext(private val plugins: List<ProtobufCompilerPlugin<in ProtobufCompilingState<*, *>>>) {
    fun advance(state: ProtobufCompilingState<*, *>) {
        plugins.forEach {
            if (it.accept(state)) {
                it.compile(this, state)
            }
        }
    }
}

abstract class BaseProtobufCompilerPlugin<T : ProtobufCompilingState<*, *>> : ProtobufCompilerPlugin<T> {
    private val target by lazy {
        when (val type = this::class.java.getTypeArgument(ProtobufCompilerPlugin::class.java, 0)) {
            is Class<*> -> type
            is ParameterizedType -> type.rawType as Class<*>
            else -> throw IllegalStateException("type argument of BaseProtobufCompilerPlugin must be a fixed class")
        }
    }

    override fun accept(state: T): Boolean {
        return target.isInstance(state)
    }
}