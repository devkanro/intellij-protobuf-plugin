package io.kanro.idea.plugin.protobuf.lang.support

object WellknownTypes {
    const val ANY = "google.protobuf.Any"
    const val STRUCT = "google.protobuf.Struct"
    const val VALUE = "google.protobuf.Value"
    const val NULL_VALUE = "google.protobuf.NullValue"
    const val LIST_VALUE = "google.protobuf.ListValue"
    const val TIMESTAMP = "google.protobuf.Timestamp"
    const val DURATION = "google.protobuf.Duration"
    const val FIELD_MASK = "google.protobuf.FieldMask"
    const val DOUBLE_VALUE = "google.protobuf.DoubleValue"
    const val FLOAT_VALUE = "google.protobuf.FloatValue"
    const val INT_64_VALUE = "google.protobuf.Int64Value"
    const val UINT_64_VALUE = "google.protobuf.UInt64Value"
    const val INT_32_VALUE = "google.protobuf.Int32Value"
    const val UINT_32_VALUE = "google.protobuf.UInt32Value"
    const val BOOL_VALUE = "google.protobuf.BoolValue"
    const val STRING_VALUE = "google.protobuf.StringValue"
    const val BYTES_VALUE = "google.protobuf.BytesValue"

    val types =
        setOf(
            ANY, STRUCT, VALUE, NULL_VALUE, LIST_VALUE,
            TIMESTAMP, DURATION, FIELD_MASK,
            DOUBLE_VALUE, FLOAT_VALUE, INT_64_VALUE,
            UINT_64_VALUE, INT_32_VALUE, UINT_32_VALUE,
            BOOL_VALUE, STRING_VALUE, BYTES_VALUE,
        )
}
