package io.kanro.idea.plugin.protobuf.buf.util

import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

object BufFiles {
    fun getCacheRoot(): Path {
        val path = System.getenv("BUF_CACHE_DIR") ?: System.getenv("XDG_CACHE_HOME") ?: if (isWindows()) {
            System.getenv("LOCALAPPDATA")
        } else {
            System.getProperty("user.home") + "/.cache"
        }
        return Path.of(path, "buf", "v1", "module", "data")
    }

    fun getRootForBufLibrary(remote: String, owner: String, repo: String, commit: String): Path {
        return getCacheRoot().resolve(Path.of(remote, owner, repo, commit))
    }

    fun isValidBufYaml(file: VirtualFile): Boolean {
        return file.isValid && isBufYaml(file.name) && !file.isDirectory
    }
}

fun isWindows(): Boolean {
    return System.getProperty("os.name").lowercase().startsWith("windows")
}

fun isBufConfiguration(name: String): Boolean {
    return isBufYaml(name) || isBufGenYaml(name) || isBufWorkYaml(name) || isBufLock(name)
}

const val BUF_YAML = "buf.yaml"
fun isBufYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_YAML -> true
        else -> false
    }
}

const val BUF_GEN_YAML = "buf.gen.yaml"
fun isBufGenYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_GEN_YAML -> true
        else -> false
    }
}

const val BUF_WORK_YAML = "buf.work.yaml"
fun isBufWorkYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_WORK_YAML -> true
        else -> false
    }
}

const val BUF_LOCK = "buf.lock"
fun isBufLock(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_LOCK -> true
        else -> false
    }
}
