package io.kanro.idea.plugin.protobuf.aip

import com.intellij.psi.util.QualifiedName

object AipOptions {
    val resourceReferenceOption =
        QualifiedName.fromComponents("google", "api", "resource_reference")
    val resourceDefinitionOption = QualifiedName.fromComponents("google", "api", "resource_definition")
    val resourceOption = QualifiedName.fromComponents("google", "api", "resource")
    val resourceTypeField = QualifiedName.fromComponents("type")
    val resourceChildTypeField = QualifiedName.fromComponents("child_type")

    val httpRuleBodyName = QualifiedName.fromComponents("google", "api", "HttpRule", "body")
    val httpRuleBodyField = QualifiedName.fromComponents("body")
    val httpRuleResponseBodyName = QualifiedName.fromComponents("google", "api", "HttpRule", "response_body")

    val lroMetadataName = QualifiedName.fromComponents("google", "longrunning", "OperationInfo", "metadata_type")
    val lroResponseName = QualifiedName.fromComponents("google", "longrunning", "OperationInfo", "response_type")

    val httpOption = QualifiedName.fromComponents("google", "api", "http")
    val httpRuleGetName = QualifiedName.fromComponents("google", "api", "HttpRule", "get")
    val httpRulePutName = QualifiedName.fromComponents("google", "api", "HttpRule", "put")
    val httpRulePostName = QualifiedName.fromComponents("google", "api", "HttpRule", "post")
    val httpRuleDeleteName = QualifiedName.fromComponents("google", "api", "HttpRule", "delete")
    val httpRulePatchName = QualifiedName.fromComponents("google", "api", "HttpRule", "patch")

    val httpRulesName =
        setOf(
            httpRuleGetName,
            httpRulePutName,
            httpRulePostName,
            httpRuleDeleteName,
            httpRulePatchName,
        )
}
