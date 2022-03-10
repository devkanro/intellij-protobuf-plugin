package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile
import io.kanro.idea.plugin.protobuf.buf.util.BUF_LOCK
import org.jetbrains.yaml.YAMLFileType

class BufLockFileTypeDetector : FileTypeRegistry.FileTypeDetector {
    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? {
        if (file.name.lowercase() == BUF_LOCK) return YAMLFileType.YML
        return null
    }
}
