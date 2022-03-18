package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufBreakingFieldSchema : BufFieldSchema(
    "breaking",
    "The `breaking` key is optional, and specifies the breaking change detection rules enforced on the files contained within the module.",
    BufBreakingSchema,
    true
)

object BufBreakingSchema : BufObjectSchema(
    listOf(
        BufBreakingUseFieldSchema,
        BufBreakingExceptFieldSchema,
        BufBreakingIgnoreFieldSchema,
        BufBreakingIgnoreFieldSchema,
        BufBreakingIgnoreOnlyFieldSchema,
        BufBreakingIgnoreUnstablePackagesFieldSchema
    )
)

object BufBreakingUseFieldSchema : BufFieldSchema(
    "use",
    "The `use` key is optional, and lists the IDs or categories to use for breaking change detection. The default value is the single item `FILE`, which is what we recommend.",
    BufArraySchema(BufEnumTypeSchema(bufBreakingRules)),
    true
)

object BufBreakingExceptFieldSchema : BufFieldSchema(
    "except",
    "The `except` key is optional, and removes IDs or categories from the use list. **We do not recommend using this option in general.**",
    BufArraySchema(BufEnumTypeSchema(bufBreakingRules)),
    true
)

object BufBreakingIgnoreFieldSchema : BufFieldSchema(
    "ignore",
    "The `ignore` key is optional, and allows directories or files to be excluded from all breaking rules when running buf breaking. The specified directory or file paths must be relative to the buf.yaml.",
    BufArraySchema(BufSchemaScalarType.STRING),
    true
)

object BufBreakingIgnoreOnlyFieldSchema : BufFieldSchema(
    "ignore_only",
    "The `ignore_only` key is optional, and allows directories or files to be excluded from specific breaking rules when running buf breaking by taking a map from breaking rule ID or category to path.",
    BufObjectSchema(
        bufBreakingRules.map {
            BufFieldSchema(it.name, it.document, BufArraySchema(BufSchemaScalarType.STRING), true)
        }
    ),
    true
)

object BufBreakingIgnoreUnstablePackagesFieldSchema : BufFieldSchema(
    "ignore_unstable_packages",
    "The `ignore_unstable_packages` key is optional, and ignores packages with a last component that is one of the unstable forms recognized by PACKAGE_VERSION_SUFFIX.",
    BufSchemaScalarType.BOOL,
    true
)

val bufBreakingRules = listOf(
    BufEnumValueSchema("WIRE", ""),
    BufEnumValueSchema("WIRE_JSON", ""),
    BufEnumValueSchema("PACKAGE", ""),
    BufEnumValueSchema("FILE", ""),
    BufEnumValueSchema("ENUM_NO_DELETE", ""),
    BufEnumValueSchema("MESSAGE_NO_DELETE", ""),
    BufEnumValueSchema("SERVICE_NO_DELETE", ""),
    BufEnumValueSchema("PACKAGE_ENUM_NO_DELETE", ""),
    BufEnumValueSchema("PACKAGE_MESSAGE_NO_DELETE", ""),
    BufEnumValueSchema("PACKAGE_SERVICE_NO_DELETE", ""),
    BufEnumValueSchema("FILE_NO_DELETE", ""),
    BufEnumValueSchema("FILE_SAME_PACKAGE", ""),
    BufEnumValueSchema("PACKAGE_NO_DELETE", ""),
    BufEnumValueSchema("ENUM_VALUE_NO_DELETE", ""),
    BufEnumValueSchema("FIELD_NO_DELETE", ""),
    BufEnumValueSchema("ENUM_VALUE_NO_DELETE_UNLESS_NUMBER_RESERVED", ""),
    BufEnumValueSchema("FIELD_NO_DELETE_UNLESS_NUMBER_RESERVED", ""),
    BufEnumValueSchema("ENUM_VALUE_NO_DELETE_UNLESS_NAME_RESERVED", ""),
    BufEnumValueSchema("FIELD_NO_DELETE_UNLESS_NAME_RESERVED", ""),
    BufEnumValueSchema("RPC_NO_DELETE", ""),
    BufEnumValueSchema("ONEOF_NO_DELETE", ""),
    BufEnumValueSchema("FILE_SAME_SYNTAX", ""),
    BufEnumValueSchema("FILE_SAME_CC_ENABLE_ARENAS", ""),
    BufEnumValueSchema("FILE_SAME_CC_GENERIC_SERVICES", ""),
    BufEnumValueSchema("FILE_SAME_CSHARP_NAMESPACE", ""),
    BufEnumValueSchema("FILE_SAME_GO_PACKAGE", ""),
    BufEnumValueSchema("FILE_SAME_JAVA_GENERIC_SERVICES", ""),
    BufEnumValueSchema("FILE_SAME_JAVA_MULTIPLE_FILES", ""),
    BufEnumValueSchema("FILE_SAME_JAVA_OUTER_CLASSNAME", ""),
    BufEnumValueSchema("FILE_SAME_JAVA_PACKAGE", ""),
    BufEnumValueSchema("FILE_SAME_JAVA_STRING_CHECK_UTF8", ""),
    BufEnumValueSchema("FILE_SAME_OBJC_CLASS_PREFIX", ""),
    BufEnumValueSchema("FILE_SAME_OPTIMIZE_FOR", ""),
    BufEnumValueSchema("FILE_SAME_PHP_CLASS_PREFIX", ""),
    BufEnumValueSchema("FILE_SAME_PHP_GENERIC_SERVICES", ""),
    BufEnumValueSchema("FILE_SAME_PHP_METADATA_NAMESPACE", ""),
    BufEnumValueSchema("FILE_SAME_PHP_NAMESPACE", ""),
    BufEnumValueSchema("FILE_SAME_PY_GENERIC_SERVICES", ""),
    BufEnumValueSchema("FILE_SAME_RUBY_PACKAGE", ""),
    BufEnumValueSchema("FILE_SAME_SWIFT_PREFIX", ""),
    BufEnumValueSchema("ENUM_VALUE_SAME_NAME", ""),
    BufEnumValueSchema("FIELD_SAME_CTYPE", ""),
    BufEnumValueSchema("FIELD_SAME_JSTYPE", ""),
    BufEnumValueSchema("FIELD_SAME_TYPE", ""),
    BufEnumValueSchema("FIELD_WIRE_COMPATIBLE_TYPE", ""),
    BufEnumValueSchema("FIELD_WIRE_JSON_COMPATIBLE_TYPE", ""),
    BufEnumValueSchema("FIELD_SAME_LABEL", ""),
    BufEnumValueSchema("FIELD_SAME_ONEOF", ""),
    BufEnumValueSchema("FIELD_SAME_NAME", ""),
    BufEnumValueSchema("FIELD_SAME_JSON_NAME", ""),
    BufEnumValueSchema("RESERVED_ENUM_NO_DELETE", ""),
    BufEnumValueSchema("RESERVED_MESSAGE_NO_DELETE", ""),
    BufEnumValueSchema("EXTENSION_MESSAGE_NO_DELETE", ""),
    BufEnumValueSchema("MESSAGE_SAME_MESSAGE_SET_WIRE_FORMAT", ""),
    BufEnumValueSchema("MESSAGE_NO_REMOVE_STANDARD_DESCRIPTOR_ACCESSOR", ""),
    BufEnumValueSchema("RPC_SAME_REQUEST_TYPE", ""),
    BufEnumValueSchema("RPC_SAME_RESPONSE_TYPE", ""),
    BufEnumValueSchema("RPC_SAME_CLIENT_STREAMING", ""),
    BufEnumValueSchema("RPC_SAME_SERVER_STREAMING", ""),
    BufEnumValueSchema("RPC_SAME_IDEMPOTENCY_LEVEL", ""),
)
