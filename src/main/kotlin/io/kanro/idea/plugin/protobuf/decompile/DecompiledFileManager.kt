package io.kanro.idea.plugin.protobuf.decompile

import com.google.protobuf.DescriptorProtos
import com.intellij.openapi.vfs.DeprecatedVirtualFileSystem
import com.intellij.openapi.vfs.NonPhysicalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.psi.PsiElement
import com.intellij.testFramework.LightVirtualFile
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import java.io.File

class DecompiledFileSystem : DeprecatedVirtualFileSystem(), NonPhysicalFileSystem {
    private val root = object : DecompiledFile() {
        override fun getFileSystem(): VirtualFileSystem {
            return this@DecompiledFileSystem
        }
    }

    fun root(): VirtualFile = root

    fun file(path: String, content: String): VirtualFile {
        var file: DecompiledFile = root
        for (component in path.replace(File.separatorChar, '/').replace('/', ':').removePrefix(":").split(':')) {
            file = file.getOrCreate(component)
        }
        file.isWritable = true
        file.setContent(null, content, false)
        file.isWritable = false
        return file
    }

    override fun getProtocol(): String {
        return "mock"
    }

    override fun findFileByPath(path: String): VirtualFile? {
        var file: VirtualFile? = root
        for (component in path.replace(File.separatorChar, '/').replace('/', ':').removePrefix(":").split(':')) {
            file = file?.findChild(component) ?: return null
        }
        return file
    }

    override fun refresh(asynchronous: Boolean) {
    }

    override fun refreshAndFindFileByPath(path: String): VirtualFile? {
        return findFileByPath(path)
    }

    private open class DecompiledFile : LightVirtualFile {
        private val parent: VirtualFile?
        private val children: MutableMap<String, DecompiledFile> = mutableMapOf()

        protected constructor() : super() {
            parent = null
        }

        constructor(parent: DecompiledFile, name: String) : super(name) {
            this.parent = parent
        }

        fun getOrCreate(file: String): DecompiledFile {
            return findChild(file)?.let { it as DecompiledFile } ?: DecompiledFile(this, file).apply {
                this@DecompiledFile.children[file] = this
            }
        }

        override fun getFileSystem(): VirtualFileSystem {
            return parent!!.fileSystem
        }

        override fun getPath(): String {
            return parent?.path?.let { "$it/$name" } ?: ""
        }

        override fun isDirectory(): Boolean {
            return parent == null || children.isNotEmpty()
        }

        override fun getParent(): VirtualFile? {
            return parent
        }

        override fun findChild(name: String): VirtualFile? {
            return children[name]
        }

        override fun getChildren(): Array<VirtualFile> {
            return children.values.toTypedArray()
        }
    }
}

object DecompiledFileManager {
    private val decompiledFileSystem = DecompiledFileSystem()

    fun findFile(element: PsiElement, file: ByteArray): VirtualFile {
        val descriptor = DescriptorProtos.FileDescriptorProto.parseFrom(file)
        FileResolver.findFile(descriptor.name, element.project, element).firstOrNull()?.let { return it }
        val proto = buildString {
            appendLine("// Decompiled by intellij protobuf plugin")
            appendLine("// source: ${element.containingFile.virtualFile.path}")
            appendLine()
            append(ProtobufDecompiler.decompile(descriptor))
        }
        return decompiledFileSystem.file(descriptor.name, proto).apply {
            isWritable = false
        }
    }

    fun findFile(name: String): VirtualFile? {
        val file = decompiledFileSystem.refreshAndFindFileByPath(name) ?: return null
        if (file.isDirectory) return null
        return file
    }

    fun root(): VirtualFile {
        return decompiledFileSystem.root()
    }
}
