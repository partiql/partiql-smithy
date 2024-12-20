@file:Suppress("unused")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Generator
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxType
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxUnit
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.core.Type

private typealias Writes = MutableList<KotlinWriter.Write>

/**
 * Generator for _file_writer.mustache; this is stateful.
 */
internal class KotlinWriter(
    symbols: KotlinSymbols,
    options: Options,
    templates: Templates,
) : Generator(symbols, templates) {

    private val _this = this
    private val domain = symbols.model.domain.pascal
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
        for (definition in symbols.model.definitions) {
            generateDefinition(definition, buffer())
        }
        val hash = object {
            val `package` = options.getString("package")!!
            val imports = options.getList("imports") { it.textValue }
            val domain = _this.domain
            val writes = _this.writes
        }
        file.write(templates.apply("_file_writer", hash))
        return file
    }

    override fun generateAliasPrimitive(alias: CtxAlias, primitive: CtxPrimitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val write = primitive.write("value.value")
        }
        val write = Write(
            write = symbols.method(alias.symbol, prefix = "write"),
            type = symbols.typeOf(alias),
            text = hash.toString("write_text_primitive"),
            packed = hash.toString("write_packed_primitive"),
        )
        writes.add(write)
    }

    override fun generateAliasArray(alias: CtxAlias, array: CtxArray, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val size = array.size?.toString() ?: "null"
            val lambda = array.item.write("it")
        }
        val write = Write(
            write = symbols.method(alias.symbol, prefix = "write"),
            type = symbols.typeOf(alias),
            text = hash.toString("write_text_array"),
            packed = hash.toString("write_packed_array"),
        )
        writes.add(write)
    }

    override fun generateAliasUnit(alias: CtxAlias, unit: CtxUnit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
        }
        val write = Write(
            write = symbols.method(alias.symbol, prefix = "write"),
            type = symbols.typeOf(alias),
            text = hash.toString("write_text_unit"),
            packed = hash.toString("write_packed_unit"),
        )
        writes.add(write)
    }

    override fun generateEnum(enum: CtxEnum, buffer: Buffer) {
        val hash = object {
            val tag = enum.symbol.tag
        }
        val write = Write(
            write = symbols.method(enum.symbol, prefix = "write"),
            type = symbols.typeOf(enum),
            text = hash.toString("write_text_enum"),
            packed = hash.toString("write_packed_enum"),
        )
        writes.add(write)
    }

    override fun generateStruct(struct: CtxStruct, buffer: Buffer) {
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
            write = symbols.method(struct.symbol, prefix = "write"),
            type = symbols.typeOf(struct),
            text = hash.toString("write_text_struct"),
            packed = hash.toString("write_packed_struct"),
        )
        writes.add(write)
    }

    override fun generateUnion(union: CtxUnion, buffer: Buffer) {
        val hash = object {
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val type = symbols.typeOf(variant)
                    val index = i
                    val write = symbols.method(variant.symbol(), prefix = "write")
                }
            }
        }
        val write = Write(
            write = symbols.method(union.symbol, prefix = "write"),
            type = symbols.typeOf(union),
            text = hash.toString("write_text_union"),
            packed = hash.toString("write_packed_union"),
        )
        writes.add(write)
        // descend
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    internal fun CtxType.write(arg: String): String {
        val args = mutableListOf(arg)
        val method = when (this) {
            is CtxArray -> {
                args += type.size?.toString() ?: "null"
                val call = "writeArray(${args.joinToString()})"
                val lambda = this.item.write("it")
                return "$call { $lambda }"
            }
            is CtxNamed -> symbols.method(symbol, prefix = "write")
            is CtxPrimitive -> when (val type = type) {
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
            is CtxUnit -> "writeUnit"
        }
        return "$method(${args.joinToString()})"
    }
}
