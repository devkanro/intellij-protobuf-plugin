package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.protobuf.LocalProtoReflection
import com.bybutter.sisyphus.protobuf.ProtoSupport
import com.bybutter.sisyphus.protobuf.booster.Booster_4CCD27CC8C541E27D15600026AA8457F
import com.bybutter.sisyphus.protobuf.dynamic.DynamicFileSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.compile.Protoc
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex

class ProtoFileReflection(private val context: ProtobufElement) : LocalProtoReflection() {
    init {
        Booster_4CCD27CC8C541E27D15600026AA8457F(this)
    }

    override fun findSupport(name: String): ProtoSupport<*>? {
        super.findSupport(name)?.let { return it }

        synchronized(this) {
            ApplicationManager.getApplication().runReadAction {
                doResolve(name.trim('.').substringAfterLast('/'))
            }
        }

        return super.findSupport(name)
    }

    private fun doResolve(name: String) {
        val elements = StubIndex.getElements(
            QualifiedNameIndex.key,
            name,
            context.project,
            GlobalSearchScope.allScope(context.project),
            ProtobufElement::class.java
        ).toList()
        val descriptorSet = Protoc.compileFiles(elements)
        descriptorSet.file.forEach {
            register(DynamicFileSupport(it))
        }
    }
}