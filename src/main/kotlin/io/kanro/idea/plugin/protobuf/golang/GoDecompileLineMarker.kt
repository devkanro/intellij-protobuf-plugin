package io.kanro.idea.plugin.protobuf.golang

import com.goide.psi.GoArrayOrSliceType
import com.goide.psi.GoCompositeLit
import com.goide.psi.GoVarDefinition
import com.goide.psi.GoVarSpec
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import java.awt.event.MouseEvent

class GoDecompileLineMarker : LineMarkerProviderDescriptor() {
    override fun getName(): String? {
        return null
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is GoVarDefinition) return null
        if (!element.text.endsWith("_proto_rawDesc")) return null
        val varSpec = element.parent as? GoVarSpec ?: return null
        val compositeLit = varSpec.expressionList.firstOrNull() as? GoCompositeLit ?: return null
        val type = compositeLit.type as? GoArrayOrSliceType ?: return null
        if (!type.type.textMatches("byte")) return null

        return ProtobufDecompileLineMarkerInfo(element)
    }
}

class ProtobufDecompileLineMarkerInfo(element: GoVarDefinition) : LineMarkerInfo<GoVarDefinition>(
    element, element.textRange, Icons.PROTO_DECOMPILE,
    {
        "Decompile protobuf descriptor"
    },
    ProtobufDecompileNavigationHandler, GutterIconRenderer.Alignment.CENTER,
    {
        "Decompile descriptor"
    }
)

object ProtobufDecompileNavigationHandler : GutterIconNavigationHandler<GoVarDefinition> {
    override fun navigate(e: MouseEvent, element: GoVarDefinition) {
        val varSpec = element.parent as? GoVarSpec ?: return
        FileEditorManager.getInstance(element.project).openFile(varSpec.decompile() ?: return, true)
    }
}
