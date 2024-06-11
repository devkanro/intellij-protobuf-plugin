package io.kanro.idea.plugin.protobuf.lang.annotator

data class ProtobufFeature(
    val enumType: ProtobufEnumType,
    val fieldPresence: ProtobufFieldPresence,
    val jsonFormat: ProtobufJsonFormat,
    val messageEncoding: ProtobufMessageEncoding,
    val repeatedFieldEncoding: ProtobufRepeatedFieldEncoding,
    val utf8Validation: ProtobufUtf8Validation,
) {
    companion object {
        val PROTO3 =
            ProtobufFeature(
                ProtobufEnumType.OPEN,
                ProtobufFieldPresence.IMPLICIT,
                ProtobufJsonFormat.ALLOW,
                ProtobufMessageEncoding.LENGTH_PREFIXED,
                ProtobufRepeatedFieldEncoding.PACKED,
                ProtobufUtf8Validation.VERIFY,
            )

        val PROTO2 =
            ProtobufFeature(
                ProtobufEnumType.CLOSED,
                ProtobufFieldPresence.EXPLICIT,
                ProtobufJsonFormat.LEGACY_BEST_EFFORT,
                ProtobufMessageEncoding.LENGTH_PREFIXED,
                ProtobufRepeatedFieldEncoding.EXPANDED,
                ProtobufUtf8Validation.NONE,
            )

        val EDITION_2023 =
            ProtobufFeature(
                ProtobufEnumType.OPEN,
                ProtobufFieldPresence.EXPLICIT,
                ProtobufJsonFormat.ALLOW,
                ProtobufMessageEncoding.LENGTH_PREFIXED,
                ProtobufRepeatedFieldEncoding.PACKED,
                ProtobufUtf8Validation.VERIFY,
            )
    }
}

enum class ProtobufEnumType {
    CLOSED,
    OPEN,
}

enum class ProtobufFieldPresence {
    LEGACY_REQUIRED,
    EXPLICIT,
    IMPLICIT,
}

enum class ProtobufJsonFormat {
    ALLOW,
    LEGACY_BEST_EFFORT,
}

enum class ProtobufMessageEncoding {
    LENGTH_PREFIXED,
    DELIMITED,
}

enum class ProtobufRepeatedFieldEncoding {
    PACKED,
    EXPANDED,
}

enum class ProtobufUtf8Validation {
    VERIFY,
    NONE,
}
