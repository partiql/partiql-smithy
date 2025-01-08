@file:Suppress("unused")

package io.github.amzn.anodizer.target.rust

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.Type

internal class RustReader(domain: Context.Domain, templates: Templates) : RustGenerator(domain, templates) {

    private val _domain: String = domain.name.pascal
    private val _reads = mutableListOf<Read>()

    internal class Read(
        @JvmField val read: String,
        @JvmField val type: String,
        @JvmField val text: String,
        @JvmField val packed: String,
    )

    override fun generate(buffer: Buffer) {
        _reads.clear()
        for (definition in domain.definitions) {
            generateDefinition(definition, buffer)
        }
        val hash = object {
            val domain = _domain
            val reads = _reads
        }
        buffer.appendTemplate("reader/trait_reader", hash)
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val read = primitive.read(primitive.type.asArg("value"))
        }
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = typeOf(alias),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_primitive"),
            // packed = hash.toString("reader/packed/fn_read_primitive"),
        )
        _reads.add(read)
    }

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {
        val hash = array.hash(
            tag = alias.symbol.tag,
            items = "value",
        )
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = typeOf(alias),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_array"),
            // packed = hash.toString("reader/packed/fn_read_array"),
        )
        _reads.add(read)
    }

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
        }
        val read = Read(
            read = method(alias.symbol, prefix = "read"),
            type = typeOf(alias),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_unit"),
            // packed = hash.toString("reader/packed/fn_read_unit"),
        )
        _reads.add(read)
    }

    override fun generateEnum(enum: Context.Enum, buffer: Buffer) {
        val hash = object {
            val tag = enum.symbol.tag
            val enum = typeOf(enum)
            val values = enum.values.map {
                object {
                    val name = it.pascal
                    val symbol = it.upper
                }
            }
        }
        val read = Read(
            read = method(enum.symbol, prefix = "read"),
            type = typeOf(enum),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_enum"),
            // packed = hash.toString("reader/packed/fn_read_enum"),
        )
        _reads.add(read)
    }

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val hash = object {
            val tag = struct.symbol.tag
            val fields = struct.fields.map {
                object {
                    val arg = it.type.fieldArg("value.${it.name.camel}")
                    val key = it.name.snake
                    val read = it.type.read(arg)
                }
            }
        }
        val read = Read(
            read = method(struct.symbol, prefix = "read"),
            type = typeOf(struct),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_struct"),
            // packed = hash.toString("reader/packed/fn_read_struct"),
        )
        _reads.add(read)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        val hash = object {
            val union = union.symbol.path.pascal
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val name = variant.symbol().name.pascal
                    val type = typeOf(variant)
                    val index = i
                    val read = method(variant.symbol(), prefix = "read")
                }
            }
        }
        val read = Read(
            read = method(union.symbol, prefix = "read"),
            type = typeOf(union),
            text = "todo!()",
            packed = "todo!()",
            // text = hash.toString("reader/text/fn_read_union"),
            // packed = hash.toString("reader/packed/fn_read_union"),
        )
        _reads.add(read)
        // descend
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    internal fun Context.Type.read(arg: String, i: Int = 0): String {
        var args = mutableListOf<String>(arg)
        val method = when (this) {
            is Context.Named -> method(symbol, prefix = "read")
            is Context.Unit -> "reader.read_unit"
            is Context.Array -> {
                return "todo!()"
                // val hash = this.hash(items = arg, i = i)
                // return templates.apply("reader/packed/fn_read_array", hash)
            }
            is Context.Primitive -> {
                args += args().map { it.optional() }
                val method = when (type) {
                    is Type.Primitive.Void -> "read_null"
                    is Type.Primitive.Bool -> "read_bool"
                    is Type.Primitive.Int -> "read_int"
                    is Type.Primitive.Float -> "read_float"
                    is Type.Primitive.Decimal -> "read_decimal"
                    is Type.Primitive.String -> "read_string"
                    is Type.Primitive.Blob -> "read_blob"
                    is Type.Primitive.Clob -> "read_clob"
                }
                "reader.$method"
            }
        }
        return "self.$method(${args.joinToString()})"
    }

    private fun Any?.optional(): String = if (this == null) "None" else "Some($this)"

    private fun Type.asArg(arg: String): String = when (this) {
        is Type.Primitive.Bool,
        is Type.Primitive.Int,
        is Type.Primitive.Float,
        -> "*$arg" // dereference certain primitives.
        else -> arg
    }

    private fun Context.Array.hash(tag: String? = null, items: String, i: Int = 0): Any {
        val array = this
        val item = "v_$i"
        val arg = this.item.type().asArg(item)
        return object {
            val tag = tag
            val item = item
            val items = items
            val size = array.size.optional()
            val read = array.item.read(arg, i + 1)
        }
    }

    /**
     * Some struct fields need to be dereferenced.
     */
    private fun Context.Type.fieldArg(arg: String): String {
        return when (this.type()) {
            is Type.Array,
            is Type.Named,
            is Type.Primitive.String,
            is Type.Primitive.Blob,
            is Type.Primitive.Clob,
            is Type.Primitive.Decimal,
            -> "&$arg"
            else -> arg
        }
    }
}
