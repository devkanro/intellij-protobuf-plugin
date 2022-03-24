package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufLintIgnoreFieldSchema : BufFieldSchema(
    "ignore",
    "The `ignore` key is optional, and allows directories or files to be excluded from all lint rules when running buf lint.",
    BufArraySchema(BufSchemaScalarType.STRING),
    true
)

object BufLintAllowCommentIgnoresFieldSchema : BufFieldSchema(
    "allow_comment_ignores",
    "The `allow_comment_ignores` key is optional, and turns on comment-driven ignores. We do not recommend using this option in general, however in some situations it is unavoidable.",
    BufSchemaScalarType.BOOL,
    true
)

object BufLintEnumZeroValueSuffixFieldSchema : BufFieldSchema(
    "enum_zero_value_suffix",
    "The `enum_zero_value_suffix` key is optional, and controls the behavior of the `ENUM_ZERO_VALUE_SUFFIX` lint rule. By default, this rule verifies that the zero value of all enums ends in `_UNSPECIFIED`, as recommended by the [Google Protobuf Style Guide](https://developers.google.com/protocol-buffers/docs/style#enums). But organizations may have a different preferred suffix, for example `_NONE`.",
    BufSchemaScalarType.STRING,
    true
)

object BufLintRpcAllowSameRequestResponseFieldSchema : BufFieldSchema(
    "rpc_allow_same_request_response",
    "The `rpc_allow_same_request_response` key is optional, and allows the same message type to be used for a single RPC's request and response type. **We do not recommend using this option in general.**",
    BufSchemaScalarType.BOOL,
    true
)

object BufLintRpcAllowGoogleProtobufEmptyRequestsFieldSchema : BufFieldSchema(
    "rpc_allow_google_protobuf_empty_requests",
    "The `rpc_allow_google_protobuf_empty_requests` key is optional, and allows RPC requests to be `google.protobuf.Empty` messages. This can be set if you want to allow messages to be void forever, that is, to never take any parameters. **We do not recommend using this option in general.**",
    BufSchemaScalarType.BOOL,
    true
)

object BufLintRpcAllowGoogleProtobufEmptyResponsesFieldSchema : BufFieldSchema(
    "rpc_allow_google_protobuf_empty_responses",
    "The `rpc_allow_google_protobuf_empty_responses` key is optional, and allows RPC responses to be `google.protobuf.Empty` messages. This can be set if you want to allow messages to never return any parameters. **We do not recommend using this option in general.**",
    BufSchemaScalarType.BOOL,
    true
)

object BufLintServiceSuffixFieldSchema : BufFieldSchema(
    "service_suffix",
    "The `service_suffix` key is optional, and controls the behavior of the `SERVICE_SUFFIX` lint rule. By default, this rule verifies that all service names are suffixed with `Service`.",
    BufSchemaScalarType.IDENTIFIER,
    true
)
