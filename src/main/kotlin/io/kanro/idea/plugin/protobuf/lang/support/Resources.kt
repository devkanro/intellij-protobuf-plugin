package io.kanro.idea.plugin.protobuf.lang.support

import com.intellij.psi.util.QualifiedName

object Resources {
    val resourceReferenceOption =
        QualifiedName.fromComponents("google", "api", "resource_reference")
    val resourceDefinitionOption = QualifiedName.fromComponents("google", "api", "resource_definition")
    val resourceOption = QualifiedName.fromComponents("google", "api", "resource")
    val resourceTypeField = QualifiedName.fromComponents("type")
}
