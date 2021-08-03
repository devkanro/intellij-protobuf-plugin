package io.kanro.idea.plugin.protobuf.golang

import com.goide.psi.GoArrayOrSliceType
import com.goide.psi.GoCompositeLit
import com.goide.psi.GoVarSpec
import com.intellij.openapi.vfs.VirtualFile
import io.kanro.idea.plugin.protobuf.decompile.DecompiledFileManager
import io.kanro.idea.plugin.protobuf.string.parseHex

fun GoVarSpec.decompile(): VirtualFile? {
    val compositeLit = this.expressionList.firstOrNull() as? GoCompositeLit ?: return null
    val type = compositeLit.type as? GoArrayOrSliceType ?: return null
    if (!type.type.textMatches("byte")) return null
    val data = compositeLit.literalValue?.elementList?.joinToString("") {
        it.text.removePrefix("0x")
    }?.parseHex() ?: return null
    return DecompiledFileManager.findFile(this, data)
}
