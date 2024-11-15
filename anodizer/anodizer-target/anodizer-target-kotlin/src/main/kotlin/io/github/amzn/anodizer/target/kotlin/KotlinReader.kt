@file:Suppress("unused")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Type

private typealias Reads = MutableList<KotlinReader.Read>

/**
 * Generator for _file_reader.mustache; this is stateful.
 */
internal class KotlinReader(
    context: Context.Domain,
    options: KotlinOptions,
    templates: Templates,
) : KotlinGenerator(context, templates) {

    private val _this = this
    private val domain = context.name.pascal
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
        for (definition in domain.definitions) {
            generateDefinition(definition, buffer())
        }
        val hash = object {
            val `package` = options.pkg.joinToString(".")
            val domain = _this.domain
            val reads = _this.reads
        }
        file.write(templates.apply("_file_reader", hash))
        return file
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val ion = primitive.ion.name
            val type = typeOf(alias)
            val read = primitive.read()
        }
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_primitive"),
            packed = hash.toString("read_packed_primitive"),
        )
        reads.add(read)
    }

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val type = typeOf(alias)
            val size = array.size
            val lambda = array.item.read()
        }
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_array"),
            packed = hash.toString("read_packed_array"),
        )
        reads.add(read)
    }

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val type = typeOf(alias)
        }
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_unit"),
            packed = hash.toString("read_packed_unit"),
        )
        reads.add(read)
    }

    override fun generateEnum(enum: Context.Enum, buffer: Buffer) {
        val hash = object {
            val tag = enum.symbol.tag
            val type = typeOf(enum)
        }
        val read = Read(
            read = method(enum.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_enum"),
            packed = hash.toString("read_packed_enum"),
        )
        reads.add(read)
    }

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val hash = object {
            val tag = struct.symbol.tag
            val type = typeOf(struct)
            val fields = struct.fields.map {
                object {
                    val arg = it.name.camel
                    val key = it.name.snake
                    val read = it.type.read()
                }
            }
        }
        val read = Read(
            read = method(struct.symbol, prefix = "read"),
            type = hash.type,
            text = hash.toString("read_text_struct"),
            packed = hash.toString("read_packed_struct"),
        )
        reads.add(read)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        val hash = object {
            val type = typeOf(union)
            val tag = union.symbol.tag
            val max = union.variants.size - 1
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val type = typeOf(variant)
                    val tag = variant.symbol().tag
                    val index = i
                    val read = method(variant.symbol(), prefix = "read")
                }
            }
        }
        val read = Read(
            read = method(union.symbol, prefix = "read"),
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

    internal fun Context.Type.read(): String {
        val args = mutableListOf<String>()
        val method = when (this) {
            is Context.Array -> {
                if (type.size != null) args += type.size.toString()
                val call = "readArray(${args.joinToString()})"
                val lambda = this.item.read()
                return "$call { $lambda }"
            }
            is Context.Named -> method(symbol, prefix = "read")
            is Context.Primitive -> when (val t = type) {
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
            is Context.Unit -> "unitValue"
        }
        return "$method(${args.joinToString()})"
    }
}
