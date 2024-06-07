package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.protobuf.LocalProtoReflection
import com.bybutter.sisyphus.protobuf.ProtoSupport
import com.bybutter.sisyphus.protobuf.booster.Booster_C290E9F00A062E043ED3250F420F82A1
import com.bybutter.sisyphus.protobuf.dynamic.DynamicFileSupport
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.compile.Protoc
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.QualifiedNameIndex

class ProtoFileReflection(private val context: ProtobufElement) : LocalProtoReflection() {
    private val wellknownProtoFiles =
        setOf(
            "google/protobuf/any.proto",
            "google/protobuf/duration.proto",
            "google/protobuf/timestamp.proto",
            "google/protobuf/field_mask.proto",
            "google/protobuf/struct.proto",
            "google/protobuf/wrappers.proto",
        )

    init {
        Booster_C290E9F00A062E043ED3250F420F82A1(this)
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
        val elements =
            StubIndex.getElements(
                QualifiedNameIndex.key,
                name,
                context.project,
                GlobalSearchScope.allScope(context.project),
                ProtobufElement::class.java,
            ).toList()
        val descriptorSet = Protoc.compileFiles(elements)
        descriptorSet.file.forEach {
            if (it.name in wellknownProtoFiles) return@forEach
            register(DynamicFileSupport(it))
        }
    }
}
