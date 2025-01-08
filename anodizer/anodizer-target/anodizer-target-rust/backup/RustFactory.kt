@file:Suppress("unused")

package io.github.amzn.anodizer.target.rust

import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.Type

internal class RustFactory(domain: Context.Domain, templates: Templates) : RustGenerator(domain, templates) {

    private val _domain: String = domain.name.pascal
    private val _methods = mutableListOf<Method>()

    internal class Method(
        @JvmField val method: String,
        @JvmField val type: String,
        @JvmField val args: String,
        @JvmField val constructor: String,
    )

    override fun generate(file: File) {
        _methods.clear()
        for (definition in domain.definitions) {
            generateDefinition(definition, buffer)
        }
        val hash = object {
            val domain = _domain
            val methods = _methods
        }
        buffer.appendTemplate("factory", hash)
    }

    override fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer) {}

    override fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer) {}

    override fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer) {}

    override fun generateEnum(enum: Context.Enum, buffer: Buffer) {}

    override fun generateStruct(struct: Context.Struct, buffer: Buffer) {
        val method = Method(
            method = run {
                var method = method(struct.symbol)
                if (lifetimes.contains(struct.symbol)) method += "<'a>"
                method
            },
            type = typeOf(struct),
            args = struct.fields.map {
                val name = it.name.snake
                val type = it.type.param()
                "$name: $type"
            }.joinToString(),
            constructor = run {
                val name = struct.symbol.path.pascal
                val args = struct.fields.map { it.name.snake }.joinToString()
                "$name { $args }"
            },
        )
        _methods.add(method)
    }

    override fun generateUnion(union: Context.Union, buffer: Buffer) {
        for (variant in union.variants) {
            generateDefinition(variant, buffer)
        }
    }

    /**
     * Some struct fields need to be dereferenced.
     */
    private fun Context.Type.param(): String {
        val arg = typeOf(this)
        return when (this.type()) {
            is Type.Named,
            is Type.Primitive.Blob,
            is Type.Primitive.Clob,
            is Type.Primitive.Decimal,
            -> "&'a $arg"
            else -> arg
        }
    }
}
