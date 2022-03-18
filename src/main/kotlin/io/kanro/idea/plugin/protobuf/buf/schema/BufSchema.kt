package io.kanro.idea.plugin.protobuf.buf.schema

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufEmptyGenYaml
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufEmptyLock
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufEmptyYaml
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLock
import io.kanro.idea.plugin.protobuf.buf.schema.v1.BufEmptyWorkYaml
import io.kanro.idea.plugin.protobuf.buf.schema.v1.BufGenYaml
import io.kanro.idea.plugin.protobuf.buf.schema.v1.BufWorkYaml
import io.kanro.idea.plugin.protobuf.buf.schema.v1.BufYaml
import io.kanro.idea.plugin.protobuf.buf.util.isBufGenYaml
import io.kanro.idea.plugin.protobuf.buf.util.isBufLock
import io.kanro.idea.plugin.protobuf.buf.util.isBufWorkYaml
import io.kanro.idea.plugin.protobuf.buf.util.isBufYaml
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLPsiElement
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YAMLSequence
import org.jetbrains.yaml.psi.YAMLValue

sealed interface BufSchema<T : YAMLPsiElement> {
    fun validate(document: T) {
    }

    fun find(name: QualifiedName, index: Int = 0): BufSchema<*>?
}

interface BufRootSchema : BufSchema<YAMLDocument> {
    val name: String
    val type: BufSchemaValueType<out YAMLValue>

    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        return type.find(name, index)
    }
}

open class BufObjectSchema(
    val fields: List<BufFieldSchema>
) : BufSchemaValueType<YAMLMapping> {
    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        if (index == name.componentCount) return this
        if (index > name.componentCount) return null

        fields.forEach {
            return it.find(name, index) ?: return@forEach
        }
        return null
    }
}

open class BufArraySchema(
    val itemType: BufSchemaValueType<out YAMLValue>
) : BufSchemaValueType<YAMLSequence> {
    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        if (index >= name.componentCount) return null
        name.components[index]?.toIntOrNull() ?: return null
        if (index + 1 >= name.componentCount) return this
        return itemType.find(name, index + 1)
    }
}

open class BufFieldSchema(
    val name: String,
    val document: String,
    val valueType: BufSchemaValueType<out YAMLValue>,
    val optional: Boolean
) : BufSchema<YAMLKeyValue> {
    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        if (index >= name.componentCount) return null
        if (name.components[index] != this.name) return null
        if (index + 1 >= name.componentCount) return this

        return valueType.find(name, index + 1)
    }
}

sealed interface BufSchemaValueType<T : YAMLValue> : BufSchema<T>

open class BufEnumTypeSchema(val values: List<BufEnumValueSchema>) : BufSchemaValueType<YAMLScalar> {
    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        return null
    }
}

open class BufEnumValueSchema(val name: String, val document: String) : BufSchema<YAMLScalar> {
    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        return null
    }
}

enum class BufSchemaScalarType : BufSchemaValueType<YAMLScalar> {
    STRING, IDENTIFIER, NUMBER, BOOL;

    override fun find(name: QualifiedName, index: Int): BufSchema<*>? {
        return null
    }
}

fun bufSchema(version: String?): BufRootSchema? {
    return when (version) {
        "v1" -> BufYaml
        null -> BufEmptyYaml
        else -> null
    }
}

fun bufLockSchema(version: String?): BufRootSchema? {
    return when (version) {
        "v1" -> BufLock
        null -> BufEmptyLock
        else -> null
    }
}

fun bufGenSchema(version: String?): BufRootSchema? {
    return when (version) {
        "v1" -> BufGenYaml
        null -> BufEmptyGenYaml
        else -> null
    }
}

fun bufWorkSchema(version: String?): BufRootSchema? {
    return when (version) {
        "v1" -> BufWorkYaml
        null -> BufEmptyWorkYaml
        else -> null
    }
}

fun bufSchema(file: String, version: String?): BufRootSchema? {
    val name = file.lowercase()
    return when {
        isBufYaml(name) -> bufSchema(version)
        isBufLock(name) -> bufLockSchema(version)
        isBufGenYaml(name) -> bufGenSchema(version)
        isBufWorkYaml(name) -> bufWorkSchema(version)
        else -> null
    }
}
