@file:Suppress("unused")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Generator
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxNamespace
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxUnit
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.core.Type

/**
 * Generator for _file_domain.mustache.
 */
internal class KotlinModel(
    symbols: KotlinSymbols,
    options: Options,
    templates: Templates,
) : Generator(symbols, templates) {

    private val _this = this
    private val domain = symbols.model.domain.pascal
    private val options = options

    fun generate(): File {
        val file = File.file("$domain.kt")
        val hash = object {
            val `package` = options.getString("package")!!
            val domain = _this.domain
            val definitions = buffer {
                for (definition in symbols.model.definitions) {
                    generateDefinition(definition, this)
                    appendLine()
                }
            }
        }
        file.write(templates.apply("_file_domain", hash))
        return file
    }

    override fun generateAliasPrimitive(alias: CtxAlias, primitive: CtxPrimitive, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(alias.parent)
            val type = symbols.typeOfPrimitive(primitive)
            val constructor = primitive.constructor()
            val write = symbols.method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_primitive", hash)
    }

    override fun generateAliasArray(alias: CtxAlias, array: CtxArray, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(alias.parent)
            val size = array.size
            val type = symbols.typeOf(array.item)
            val write = symbols.method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_array", hash)
    }

    override fun generateAliasUnit(alias: CtxAlias, unit: CtxUnit, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(alias.parent)
            val write = symbols.method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_unit", hash)
    }

    override fun generateEnum(enum: CtxEnum, buffer: Buffer) {
        val hash = object {
            val name = enum.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(enum.parent)
            val values = enum.values.map { it.upper }
            val write = symbols.method(enum.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_enum", hash)
    }

    override fun generateNamespace(namespace: CtxNamespace, buffer: Buffer) {
        val hash = object {
            val namespace = namespace.symbol.name.pascal
            val definitions = buffer {
                for (definition in namespace.definitions) {
                    generateDefinition(definition, buffer)
                    appendLine()
                }
            }
        }
        buffer.appendTemplate("namespace", hash)
    }

    override fun generateStruct(struct: CtxStruct, buffer: Buffer) {
        val hash = object {
            val definition = struct.definition
            val name = struct.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(struct.parent)
            val write = symbols.method(struct.symbol, prefix = "write")
            val fields = struct.fields.map {
                object {
                    val name = it.name.camel
                    val type = symbols.typeOf(it.type)
                }
            }
        }
        buffer.appendTemplate("typedef_struct", hash)
    }

    override fun generateUnion(union: CtxUnion, buffer: Buffer) {
        val hash = object {
            val name = union.symbol.name.pascal
            val domain = _this.domain
            val parent = symbols.pathToOrNull(union.parent)
            val write = symbols.method(union.symbol, prefix = "write")
            val variants = buffer {
                for (variant in union.variants) {
                    generateDefinition(variant, buffer)
                    appendLine()
                }
            }
        }
        buffer.appendTemplate("typedef_union", hash)
    }

    // could be added to KotlinSymbols.
    internal fun CtxPrimitive.constructor(): String {
        val args: MutableList<String> = mutableListOf("value")
        val constructor = when (val type = type) {
            is Type.Primitive.Void -> TODO("void type not supported")
            is Type.Primitive.Bool -> "IonBool"
            is Type.Primitive.Int -> "IonInt"
            is Type.Primitive.Decimal -> {
                if (type.precision != null) args.add(type.precision.toString())
                if (type.exponent != null) args.add(type.exponent.toString())
                "IonDecimal"
            }
            is Type.Primitive.Float -> "IonFloat"
            is Type.Primitive.String -> "IonString"
            is Type.Primitive.Blob -> {
                if (type.size != null) args.add(type.size.toString())
                "IonBlob"
            }
            is Type.Primitive.Clob -> {
                if (type.size != null) args.add(type.size.toString())
                "IonClob"
            }
        }
        return "${constructor}(${args.joinToString()})"
    }
}
