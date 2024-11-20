@file:Suppress("unused")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Buffer
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.buffer
import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.codegen.context.CtxNamespace
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxUnit
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options

/**
 * Generator for _file_domain.mustache.
 */
internal class KotlinModel(
    model: CtxModel,
    options: Options,
    templates: Templates,
) : KotlinGenerator(model, templates) {

    private val _this = this
    private val domain = model.domain.pascal
    private val options = options

    fun generate(): File {
        val file = File.file("$domain.kt")
        val hash = object {
            val `package` = options.getString("package")!!
            val domain = _this.domain
            val definitions = buffer {
                for (definition in model.definitions) {
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
            val parent = pathToOrNull(alias.parent)
            val type = typeOfPrimitive(primitive)
            val constructor = primitive.constructor()
            val write = method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_primitive", hash)
    }

    override fun generateAliasArray(alias: CtxAlias, array: CtxArray, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.name.pascal
            val domain = _this.domain
            val parent = pathToOrNull(alias.parent)
            val size = array.size
            val type = typeOf(array.item)
            val write = method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_array", hash)
    }

    override fun generateAliasUnit(alias: CtxAlias, unit: CtxUnit, buffer: Buffer) {
        val hash = object {
            val name = alias.symbol.name.pascal
            val domain = _this.domain
            val parent = pathToOrNull(alias.parent)
            val write = method(alias.symbol, prefix = "write")
        }
        buffer.appendTemplate("typedef_unit", hash)
    }

    override fun generateEnum(enum: CtxEnum, buffer: Buffer) {
        val hash = object {
            val name = enum.symbol.name.pascal
            val domain = _this.domain
            val parent = pathToOrNull(enum.parent)
            val values = enum.values.map { it.upper }
            val write = method(enum.symbol, prefix = "write")
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
            val parent = pathToOrNull(struct.parent)
            val write = method(struct.symbol, prefix = "write")
            val fields = struct.fields.map {
                object {
                    val name = it.name.camel
                    val type = typeOf(it.type)
                }
            }
        }
        buffer.appendTemplate("typedef_struct", hash)
    }

    override fun generateUnion(union: CtxUnion, buffer: Buffer) {
        val hash = object {
            val name = union.symbol.name.pascal
            val domain = _this.domain
            val parent = pathToOrNull(union.parent)
            val write = method(union.symbol, prefix = "write")
            val variants = buffer {
                for (variant in union.variants) {
                    generateDefinition(variant, buffer)
                    appendLine()
                }
            }
        }
        buffer.appendTemplate("typedef_union", hash)
    }
}
