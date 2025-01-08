@file:Suppress("unused")

package io.github.amzn.anodizer.target.rust

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.Type

internal class RustWriter(domain: Context.Domain, templates: Templates) : RustGenerator(domain, templates) {

    private val _domain: String = domain.name.pascal
    private val _writes = mutableListOf<Write>()

    internal class Write(
        @JvmField val write: String,
        @JvmField val type: String,
        @JvmField val text: String,
        @JvmField val packed: String,
    )

    override fun generate(buffer: Buffer) {
        _writes.clear()
        for (definition in domain.definitions) {
            generateDefinition(definition, buffer)
        }
        val hash = object {
            val domain = _domain
            val writes = _writes
        }
        buffer.appendTemplate("writer/trait_writer", hash)
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
            val write = primitive.write(primitive.type.asArg("value"))
        }
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("writer/text/fn_write_primitive"),
            packed = hash.toString("writer/packed/fn_write_primitive"),
        )
        _writes.add(write)
    }

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {
        val hash = array.hash(
            tag = alias.symbol.tag,
            items = "value",
        )
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("writer/text/fn_write_array"),
            packed = hash.toString("writer/packed/fn_write_array"),
        )
        _writes.add(write)
    }

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {
        val hash = object {
            val tag = alias.symbol.tag
        }
        val write = Write(
            write = method(alias.symbol, prefix = "write"),
            type = typeOf(alias),
            text = hash.toString("writer/text/fn_write_unit"),
            packed = hash.toString("writer/packed/fn_write_unit"),
        )
        _writes.add(write)
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
        val write = Write(
            write = method(enum.symbol, prefix = "write"),
            type = typeOf(enum),
            text = hash.toString("writer/text/fn_write_enum"),
            packed = hash.toString("writer/packed/fn_write_enum"),
        )
        _writes.add(write)
    }

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val hash = object {
            val tag = struct.symbol.tag
            val fields = struct.fields.map {
                object {
                    val arg = it.type.fieldArg("value.${it.name.camel}")
                    val key = it.name.snake
                    val write = it.type.write(arg)
                }
            }
        }
        val write = Write(
            write = method(struct.symbol, prefix = "write"),
            type = typeOf(struct),
            text = hash.toString("writer/text/fn_write_struct"),
            packed = hash.toString("writer/packed/fn_write_struct"),
        )
        _writes.add(write)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        val hash = object {
            val union = union.symbol.path.pascal
            val variants = union.variants.mapIndexed { i, variant ->
                object {
                    val name = variant.symbol().name.pascal
                    val type = typeOf(variant)
                    val index = i
                    val write = method(variant.symbol(), prefix = "write")
                }
            }
        }
        val write = Write(
            write = method(union.symbol, prefix = "write"),
            type = typeOf(union),
            text = hash.toString("writer/text/fn_write_union"),
            packed = hash.toString("writer/packed/fn_write_union"),
        )
        _writes.add(write)
        // descend
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    internal fun Context.Type.write(arg: String, i: Int = 0): String {
        var args = mutableListOf<String>(arg)
        val method = when (this) {
            is Context.Named -> method(symbol, prefix = "write")
            is Context.Unit -> "writer.write_unit"
            is Context.Array -> {
                val hash = this.hash(items = arg, i = i)
                return templates.apply("writer/packed/fn_write_array", hash)
            }
            is Context.Primitive -> {
                args += args().map { it.optional() }
                val method = when (type) {
                    is Type.Primitive.Void -> "write_null"
                    is Type.Primitive.Bool -> "write_bool"
                    is Type.Primitive.Int -> "write_int"
                    is Type.Primitive.Float -> "write_float"
                    is Type.Primitive.Decimal -> "write_decimal"
                    is Type.Primitive.String -> "write_string"
                    is Type.Primitive.Blob -> "write_blob"
                    is Type.Primitive.Clob -> "write_clob"
                }
                "writer.$method"
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
            val write = array.item.write(arg, i + 1)
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
