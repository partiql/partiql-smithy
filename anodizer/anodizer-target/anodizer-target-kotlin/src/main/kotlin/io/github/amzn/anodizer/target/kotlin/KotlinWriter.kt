@file:Suppress("unused")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Type

private typealias Writes = MutableList<KotlinWriter.Write>

/**
 * Generator for _file_writer.mustache; this is stateful.
 */
internal class KotlinWriter(
    context: Context.Domain,
    options: KotlinOptions,
    templates: Templates,
) : KotlinGenerator(context, templates) {

    private val _this = this
    private val domain = context.name.pascal
    private val options = options
    private val writes = mutableListOf<Write>()

    internal class Write(
        @JvmField val write: String,
        @JvmField val type: String,
        @JvmField val text: String,
        @JvmField val packed: String,
    )

    fun generate(): File {
        writes.clear()
        val file = File.file("${domain}Writer.kt")
        for (definition in context.definitions) {
            generateDefinition(definition, buffer())
        }
        val hash = object {
            val domain = _this.domain
            val writes = _this.writes
        }
        file.write(templates.apply("_file_writer", hash))
        return file
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val write = primitive.write("value.value")
        }
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("write_text_primitive"),
            packed = hash.toString("write_packed_primitive"),
        )
        writes.add(write)
    }

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val size = array.size?.toString() ?: "null"
            val lambda = array.item.write("it")
        }
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("write_text_array"),
            packed = hash.toString("write_packed_array"),
        )
        writes.add(write)
    }

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
        }
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("write_text_unit"),
            packed = hash.toString("write_packed_unit"),
        )
        writes.add(write)
    }

    override fun generateEnum(enum: Context.Enum, buffer: Buffer) {
        val hash = object {
            val tag = enum.symbol.tag
        }
        val write = Write(
            write = method(enum.symbol, prefix = "write"),
            type = typeOf(enum),
            text = hash.toString("write_text_enum"),
            packed = hash.toString("write_packed_enum"),
        )
        writes.add(write)
    }

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val hash = object {
            val tag = struct.symbol.tag
            val fields = struct.fields.map {
                object {
                    val arg = "value.${it.name.camel}"
                    val key = it.name.snake
                    val write = it.type.write(arg)
                }
            }
        }
        val write = Write(
            write = method(struct.symbol, prefix = "write"),
            type = typeOf(struct),
            text = hash.toString("write_text_struct"),
            packed = hash.toString("write_packed_struct"),
        )
        writes.add(write)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        val hash = object {
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val type = typeOf(variant)
                    val index = i
                    val write = method(variant.symbol(), prefix = "write")
                }
            }
        }
        val write = Write(
            write = method(union.symbol, prefix = "write"),
            type = typeOf(union),
            text = hash.toString("write_text_union"),
            packed = hash.toString("write_packed_union"),
        )
        writes.add(write)
        // descend
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    internal fun Context.Type.write(arg: String): String {
        var args = mutableListOf(arg)
        val method = when (this) {
            is Context.Array -> {
                args += type.size?.toString() ?: "null"
                val call = "writeArray(${args.joinToString()})"
                val lambda = this.item.write("it")
                return "$call { $lambda }"
            }
            is Context.Named -> method(symbol, prefix = "write")
            is Context.Primitive -> when (val type = type) {
                is Type.Primitive.Void -> "writer.writeNull"
                is Type.Primitive.Bool -> "writer.writeBool"
                is Type.Primitive.Int -> "writer.writeInt"
                is Type.Primitive.Float -> "writer.writeFloat"
                is Type.Primitive.Decimal -> {
                    if (type.precision != null || type.exponent != null) {
                        args += type.precision?.toString() ?: "null"
                        args += type.exponent?.toString() ?: "null"
                        "writeDecimal"
                    } else {
                        "writer.writeDecimal"
                    }
                }
                is Type.Primitive.String -> "writer.writeString"
                is Type.Primitive.Blob -> {
                    if (type.size != null) {
                        args += type.size.toString()
                        "writeBlob"
                    } else {
                        "writer.writeBlob"
                    }
                }
                is Type.Primitive.Clob -> {
                    if (type.size != null) {
                        args += type.size.toString()
                        "writeClob"
                    } else {
                        "writer.writeClob"
                    }
                }
            }
            is Context.Unit -> "writeUnit"
        }
        return "$method(${args.joinToString()})"
    }
}
