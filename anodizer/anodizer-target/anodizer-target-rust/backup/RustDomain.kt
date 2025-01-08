@file:Suppress("unused")

package io.github.amzn.anodizer.target.rust

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.core.Type

/**
 * Generator for _file_domain.mustache
 */
internal class RustDomain(domain: Context.Domain, templates: Templates) : RustGenerator(domain, templates) {

    private val _this = this

    override fun generate(buffer: Buffer) {
        val hash = object {
            val definitions = buffer {
                for (definition in _this.domain.definitions) {
                    generateDefinition(definition, this)
                    appendLine()
                }
            }
        }
        buffer.appendTemplate("_file_domain", hash)
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.path.pascal
            val type = typeOfPrimitive(primitive)
        }
        buffer.appendTemplate("typedef_primitive", hash)
    }

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.path.pascal
            val size = array.size
            val item = typeOf(array.item)
        }
        buffer.appendTemplate("typedef_array", hash)
    }

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.path.pascal
        }
        buffer.appendTemplate("typedef_unit", hash)
    }

    override fun generateEnum(enum: Context.Enum, buffer: Buffer) {
        val hash = object {
            val name = enum.symbol.path.pascal
            val values = enum.values.map { it.pascal }
        }
        buffer.appendTemplate("typedef_enum", hash)
    }

    override fun generateNamespace(namespace: Context.Namespace, buffer: Buffer) {
        val hash = object {
            val namespace = namespace.symbol.path.pascal
            val definitions = buffer {
                for (definition in domain.definitions) {
                    generateDefinition(definition, this)
                    appendLine()
                }
            }
        }
        buffer.appendTemplate("namespace", hash)
    }

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val hash = object {
            val definition = struct.definition
            val name = pathTo(struct.symbol)
            val fields = struct.fields.map {
                object {
                    val name = it.name.camel
                    val type = it.type.fieldType()
                }
            }
        }
        buffer.appendTemplate("typedef_struct", hash)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        val hash = object {
            val name = pathTo(union.symbol)
            val definitions = buffer {
                for (variant in union.variants) {
                    generateDefinition(variant, this)
                    appendLine()
                }
            }
            val variants = union.variants.map {
                object {
                    val name = it.symbol().name.pascal
                    val arg = typeOf(it)
                }
            }
        }
        buffer.appendTemplate("typedef_union", hash)
    }

    /**
     * A struct field has ownership over all types except named, decimal, blob, and clob.
     */
    private fun Context.Type.fieldType(): String {
        val type = typeOf(this)
        return when (this.type()) {
            is Type.Named,
            is Type.Primitive.Blob,
            is Type.Primitive.Clob,
            is Type.Primitive.Decimal -> "&'a $type"
            else -> type
        }
    }
}
