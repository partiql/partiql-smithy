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

private typealias Reads = MutableList<KotlinReader.Read>

/**
 * Generator for _file_reader.mustache; this is stateful.
 */
internal class KotlinReader(
    symbols: KotlinSymbols,
    options: Options,
    templates: Templates,
) : Generator(symbols, templates) {

    private val _this = this
    private val domain = symbols.model.domain.pascal
    private val options = options
    private val reads = mutableListOf<Read>()

    internal class Read(
        @JvmField val read: String,
        @JvmField val type: String,
        @JvmField val text: String,
        @JvmField val packed: String,
    )

    fun generate(): File {
        reads.clear()
        val file = File.file("${domain}Reader.kt")
        for (definition in symbols.model.definitions) {
            generateDefinition(definition, buffer())
        }
        val hash = object {
            val `package` = options.getString("package")!!
            val domain = _this.domain
            val reads = _this.reads
        }
        file.write(templates.apply("_file_reader", hash))
        return file
    }

    override fun generateAliasPrimitive(alias: CtxAlias, primitive: CtxPrimitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val ion = primitive.ion.name
            val type = symbols.typeOf(alias)
            val read = primitive.read()
        }
        val read = Read(
            read = symbols.method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_primitive"),
            packed = hash.toString("read_packed_primitive"),
        )
        reads.add(read)
    }

    override fun generateAliasArray(alias: CtxAlias, array: CtxArray, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val type = symbols.typeOf(alias)
            val size = array.size
            val lambda = array.item.read()
        }
        val read = Read(
            read = symbols.method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_array"),
            packed = hash.toString("read_packed_array"),
        )
        reads.add(read)
    }

    override fun generateAliasUnit(alias: CtxAlias, unit: CtxUnit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val type = symbols.typeOf(alias)
        }
        val read = Read(
            read = symbols.method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_unit"),
            packed = hash.toString("read_packed_unit"),
        )
        reads.add(read)
    }

    override fun generateEnum(enum: CtxEnum, buffer: Buffer) {
        val hash = object {
            val tag = enum.symbol.tag
            val type = symbols.typeOf(enum)
        }
        val read = Read(
            read = symbols.method(enum.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_enum"),
            packed = hash.toString("read_packed_enum"),
        )
        reads.add(read)
    }

    override fun generateStruct(struct: CtxStruct, buffer: Buffer) {
        val hash = object {
            val tag = struct.symbol.tag
            val type = symbols.typeOf(struct)
            val fields = struct.fields.map {
                object {
                    val arg = it.name.camel
                    val key = it.name.snake
                    val read = it.type.read()
                }
            }
        }
        val read = Read(
            read = symbols.method(struct.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_struct"),
            packed = hash.toString("read_packed_struct"),
        )
        reads.add(read)
    }

    override fun generateUnion(union: CtxUnion, buffer: Buffer) {
        val hash = object {
            val type = symbols.typeOf(union)
            val tag = union.symbol.tag
            val max = union.variants.size - 1
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val type = symbols.typeOf(variant)
                    val tag = variant.symbol().tag
                    val index = i
                    val read = symbols.method(variant.symbol(), prefix = "read")
                }
            }
        }
        val read = Read(
            read = symbols.method(union.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_union"),
            packed = hash.toString("read_packed_union"),
        )
        reads.add(read)
        // descend
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    internal fun CtxType.read(): String {
        val args = mutableListOf<String>()
        val method = when (this) {
            is CtxArray -> {
                if (type.size != null) args += type.size.toString()
                val call = "readArray(${args.joinToString()})"
                val lambda = this.item.read()
                return "$call { $lambda }"
            }
            is CtxNamed -> symbols.method(symbol, prefix = "read")
            is CtxPrimitive -> when (val t = type) {
                is Type.Primitive.Void -> "reader.nullValue"
                is Type.Primitive.Bool -> "reader.booleanValue"
                is Type.Primitive.Int -> "reader.longValue"
                is Type.Primitive.Float -> "reader.doubleValue"
                is Type.Primitive.Decimal -> {
                    if (t.precision != null || t.exponent != null) {
                        args += t.precision?.toString() ?: "null"
                        args += t.exponent?.toString() ?: "null"
                        "decimalValue"
                    } else {
                        "reader.decimalValue"
                    }
                }
                is Type.Primitive.String -> "reader.stringValue"
                is Type.Primitive.Blob -> {
                    if (t.size != null) {
                        args += t.size.toString()
                        "newBytes"
                    } else {
                        "reader.newBytes"
                    }
                }
                is Type.Primitive.Clob -> {
                    if (t.size != null) {
                        args += t.size.toString()
                        "newBytes"
                    } else {
                        "reader.newBytes"
                    }
                }
            }
            is CtxUnit -> "unitValue"
        }
        return "$method(${args.joinToString()})"
    }
}
