package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintAllowCommentIgnoresFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintEnumZeroValueSuffixFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintIgnoreFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintRpcAllowGoogleProtobufEmptyRequestsFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintRpcAllowGoogleProtobufEmptyResponsesFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintRpcAllowSameRequestResponseFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLintServiceSuffixFieldSchema

object BufLintFieldSchema : BufFieldSchema(
    "lint",
    "The `lint` key is optional, and specifies the lint rules enforced on the files in the module.",
    BufLintSchema,
    true
)

object BufLintSchema : BufObjectSchema(
    listOf(
        BufLintUseFieldSchema,
        BufLintExceptFieldSchema,
        BufLintIgnoreFieldSchema,
        BufLintIgnoreOnlyFieldSchema,
        BufLintAllowCommentIgnoresFieldSchema,
        BufLintEnumZeroValueSuffixFieldSchema,
        BufLintRpcAllowSameRequestResponseFieldSchema,
        BufLintRpcAllowGoogleProtobufEmptyRequestsFieldSchema,
        BufLintRpcAllowGoogleProtobufEmptyResponsesFieldSchema,
        BufLintServiceSuffixFieldSchema
    )
)

object BufLintUseFieldSchema : BufFieldSchema(
    "use",
    "The `use` key is optional, and lists the IDs or categories to use for linting.",
    BufArraySchema(BufEnumTypeSchema(bufLintRules)),
    true
)

object BufLintExceptFieldSchema : BufFieldSchema(
    "except",
    "The `except` key is optional, and removes IDs or categories from the use list.",
    BufArraySchema(BufEnumTypeSchema(bufLintRules)),
    true
)

object BufLintIgnoreOnlyFieldSchema : BufFieldSchema(
    "ignore_only",
    "The `ignore_only` key is optional, and allows directories or files to be excluded from specific lint rules when running buf lint by taking a map from lint rule ID or category to path. As with ignore, the paths must be relative to the buf.yaml.",
    BufObjectSchema(
        bufLintRules.map {
            BufFieldSchema(it.name, it.document, BufArraySchema(BufSchemaScalarType.STRING), true)
        }
    ),
    true
)

val bufLintRules = listOf(
    BufEnumValueSchema(
        "MINIMAL",
        "The MINIMAL category includes DIRECTORY_SAME_PACKAGE, PACKAGE_DEFINED, PACKAGE_DIRECTORY_MATCH, PACKAGE_SAME_DIRECTORY rules."
    ),
    BufEnumValueSchema(
        "DIRECTORY_SAME_PACKAGE",
        "This rule checks that all files in a given directory are in the same package."
    ),
    BufEnumValueSchema("PACKAGE_DEFINED", "This rule checks that all files have a package declaration."),
    BufEnumValueSchema("PACKAGE_SAME_DIRECTORY", "This rule checks that all files have a package declaration."),
    BufEnumValueSchema(
        "PACKAGE_DIRECTORY_MATCH",
        "This rule checks that all files are in a directory that matches their package name."
    ),
    BufEnumValueSchema(
        "BASIC",
        "The BASIC category includes everything from the MINIMAL category, and adds basic style rules that are widely accepted as standard Protobuf style. These rules should generally be applied for all Protobuf schemas."
    ),
    BufEnumValueSchema("ENUM_PASCAL_CASE", "This rule checks that enums are PascalCase."),

    BufEnumValueSchema("ENUM_VALUE_UPPER_SNAKE_CASE", "This rule checks that enum values are UPPER_SNAKE_CASE."),

    BufEnumValueSchema("FIELD_LOWER_SNAKE_CASE", "This rule checks that field names are lower_snake_case."),

    BufEnumValueSchema("MESSAGE_PASCAL_CASE", "This rule checks that messages are PascalCase."),

    BufEnumValueSchema("ONEOF_LOWER_SNAKE_CASE", "This rule checks that oneof names are lower_snake_case."),

    BufEnumValueSchema("PACKAGE_LOWER_SNAKE_CASE", "This rule checks that packages are lower_snake.case."),

    BufEnumValueSchema("RPC_PASCAL_CASE", "This rule checks that RPCs are PascalCase."),

    BufEnumValueSchema("SERVICE_PASCAL_CASE", "This rule checks that services are PascalCase."),

    BufEnumValueSchema(
        "PACKAGE_SAME_CSHARP_NAMESPACE",
        "checks that all files with a given package have the same value for the csharp_namespace option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_GO_PACKAGE",
        "checks that all files with a given package have the same value for the go_package option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_JAVA_MULTIPLE_FILES",
        "checks that all files with a given package have the same value for the java_multiple_files option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_JAVA_PACKAGE",
        "checks that all files with a given package have the same value for the java_package option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_PHP_NAMESPACE",
        "checks that all files with a given package have the same value for the php_namespace option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_RUBY_PACKAGE",
        "checks that all files with a given package have the same value for the ruby_package option."
    ),
    BufEnumValueSchema(
        "PACKAGE_SAME_SWIFT_PREFIX",
        "checks that all files with a given package have the same value for the swift_prefix` option."
    ),

    BufEnumValueSchema("ENUM_FIRST_VALUE_ZERO", "This rule enforces that the first enum value is the zero value."),

    BufEnumValueSchema("ENUM_NO_ALLOW_ALIAS", "This rule outlaws aliased enums."),

    BufEnumValueSchema(
        "IMPORT_NO_WEAK",
        "This rule outlaws declaring imports as weak. If you didn't know this was possible, forget what you just learned in this sentence, and regardless do not use these."
    ),
    BufEnumValueSchema(
        "IMPORT_NO_PUBLIC",
        "Similar to the IMPORT_NO_WEAK rule, this rule outlaws declaring imports as public. If you didn't know this was possible, forget what you just learned in this sentence, and regardless do not use these."
    ),

    BufEnumValueSchema(
        "DEFAULT",
        "The DEFAULT category includes everything from the BASIC category, as well as some other default style rules."
    ),
    BufEnumValueSchema(
        "ENUM_VALUE_PREFIX",
        "This rule requires that all enum value names are prefixed with the enum name."
    ),
    BufEnumValueSchema(
        "ENUM_ZERO_VALUE_SUFFIX",
        "This rule requires that all enum values have a zero value of ENUM_NAME_UNSPECIFIED."
    ),
    BufEnumValueSchema(
        "FILE_LOWER_SNAKE_CASE",
        "This rule says that all .proto files must be named in lower_snake_case.proto. This is the widely accepted standard."
    ),
    BufEnumValueSchema("RPC_REQUEST_STANDARD_NAME", "These rules enforce the message name of RPC request."),
    BufEnumValueSchema("RPC_RESPONSE_STANDARD_NAME", "These rules enforce the message name of RPC responses."),
    BufEnumValueSchema(
        "RPC_REQUEST_RESPONSE_UNIQUE",
        "These rules enforce the message name of RPC request/responses are unique."
    ),
    BufEnumValueSchema(
        "PACKAGE_VERSION_SUFFIX",
        "This rule enforces that the last component of a package must be a version of the form v\\d+, v\\d+test.*, v\\d+(alpha|beta)\\d*, or v\\d+p\\d+(alpha|beta)\\d*, where numbers are >=1."
    ),
    BufEnumValueSchema("SERVICE_SUFFIX", "This rule enforces that all services are suffixed with Service."),
    BufEnumValueSchema(
        "COMMENTS",
        "This is an extra top-level category that enforces that comments are present on various parts of your Protobuf schema."
    ),
    BufEnumValueSchema("COMMENT_ENUM", "This rule checks that enums have non-empty comments."),
    BufEnumValueSchema("COMMENT_ENUM_VALUE", "This rule checks that enum values have non-empty comments."),
    BufEnumValueSchema("COMMENT_FIELD", "This rule checks that fields have non-empty comments."),
    BufEnumValueSchema("COMMENT_MESSAGE", "This rule checks that messages have non-empty comments."),

    BufEnumValueSchema("COMMENT_ONEOF", "This rule checks that oneof have non-empty comments."),

    BufEnumValueSchema("COMMENT_RPC", "This rule checks that RPCs have non-empty comments."),

    BufEnumValueSchema("COMMENT_SERVICE", "This rule checks that services have non-empty comments."),

    BufEnumValueSchema("UNARY_RPC", "This is an extra top-level category that outlaws streaming RPCs."),

    BufEnumValueSchema("RPC_NO_CLIENT_STREAMING", "checks that RPCs are not client streaming."),
    BufEnumValueSchema("RPC_NO_SERVER_STREAMING", "checks that RPCs are not server streaming."),
    BufEnumValueSchema(
        "PACKAGE_NO_IMPORT_CYCLE",
        "This is an extra uncategorized rule that detects package import cycles."
    )
)
