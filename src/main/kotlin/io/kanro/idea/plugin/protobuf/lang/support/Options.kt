package io.kanro.idea.plugin.protobuf.lang.support

import com.intellij.psi.util.QualifiedName

enum class Options(val messageName: String) {
    FILE_OPTIONS("FileOptions"),
    MESSAGE_OPTIONS("MessageOptions"),
    FIELD_OPTIONS("FieldOptions"),
    ONEOF_OPTIONS("OneofOptions"),
    ENUM_OPTIONS("EnumOptions"),
    ENUM_VALUE_OPTIONS("EnumValueOptions"),
    SERVICE_OPTIONS("ServiceOptions"),
    METHOD_OPTIONS("MethodOptions"),
    ;

    val qualifiedName by lazy {
        packageName.append(messageName)
    }

    companion object {
        val packageName = QualifiedName.fromComponents("google", "protobuf")
        val all = Options.values().map { it.qualifiedName }.toSet()
    }
}
