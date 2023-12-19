package io.kanro.idea.plugin.protobuf.aip

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.nullCachedValue
import io.kanro.idea.plugin.protobuf.lang.psi.stringValue

internal fun ProtobufRpcDefinition.transcodingBody(): String? {
    return CachedValuesManager.getCachedValue(this) {
        val option =
            options(AipOptions.httpOption).lastOrNull()
                ?: return@getCachedValue nullCachedValue()

        val result = option.value(AipOptions.httpRuleBodyField)?.stringValue() ?: ""
        CachedValueProvider.Result.create(result, PsiModificationTracker.MODIFICATION_COUNT)
    }
}

internal fun ProtobufRpcDefinition.resolveInput(): ProtobufMessageDefinition? {
    return CachedValuesManager.getCachedValue(this) {
        val input =
            this.rpcIOList.firstOrNull()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
                ?: return@getCachedValue nullCachedValue()
        return@getCachedValue CachedValueProvider.Result.create(input, PsiModificationTracker.MODIFICATION_COUNT)
    }
}
