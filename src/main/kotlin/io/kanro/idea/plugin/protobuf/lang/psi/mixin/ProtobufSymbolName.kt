package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElementBase
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufBuiltInOptionReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufEnumValueReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufFieldReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufImportReference
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolReference

abstract class ProtobufSymbolNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufSymbolName {
    override fun getReference(): PsiReference {
        return ProtobufSymbolReference(this)
    }
}

abstract class ProtobufBuiltInOptionMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufBuiltInOptionName {
    override fun getReference(): PsiReference {
        return ProtobufBuiltInOptionReference(this)
    }
}

abstract class ProtobufEnumValueMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufEnumValue {
    override fun getReference(): PsiReference {
        return ProtobufEnumValueReference(this)
    }
}

abstract class ProtobufImportStatementMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufImportStatement {
    override fun getReference(): PsiReference {
        return ProtobufImportReference(this)
    }
}

abstract class ProtobufFieldNameMixin(node: ASTNode) : ProtobufElementBase(node), ProtobufFieldName {
    override fun getReference(): PsiReference {
        return ProtobufFieldReference(this)
    }
}
