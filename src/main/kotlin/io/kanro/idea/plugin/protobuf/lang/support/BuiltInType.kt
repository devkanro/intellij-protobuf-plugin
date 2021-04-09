package io.kanro.idea.plugin.protobuf.lang.support

enum class BuiltInType {
    STRING,
    BYTES,
    BOOL,
    DOUBLE,
    FLOAT,
    UINT32,
    UINT64,
    FIXED32,
    FIXED64,
    INT32,
    INT64,
    SINT32,
    SINT64,
    SFIXED32,
    SFIXED64;

    fun value(): String {
        return this.name.toLowerCase()
    }

    companion object {
        private val types: Map<String, BuiltInType> = values().associateBy { it.name.toLowerCase() }

        fun isBuiltInType(name: String): Boolean {
            return types[name] != null
        }
    }
}
