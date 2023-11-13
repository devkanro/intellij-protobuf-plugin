package io.kanro.idea.plugin.protobuf.grpc.request

import com.intellij.httpClient.execution.common.CommonClientBodyFileHint
import com.intellij.json.JsonFileType
import com.intellij.openapi.fileTypes.FileType

class GeneralBodyFileHint(
    override val fileExtensionHint: String?,
    override val fileNameHint: String?,
    override val fileTypeHint: FileType?,
) : CommonClientBodyFileHint

fun jsonBodyFileHint(fileName: String): GeneralBodyFileHint {
    return if (fileName.endsWith(".json")) {
        GeneralBodyFileHint("json", fileName, JsonFileType.INSTANCE)
    } else {
        GeneralBodyFileHint("json", "$fileName.json", JsonFileType.INSTANCE)
    }
}
