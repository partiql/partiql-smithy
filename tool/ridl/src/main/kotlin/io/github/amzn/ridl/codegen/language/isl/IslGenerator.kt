package io.github.amzn.ridl.codegen.language.isl

import io.github.amzn.ridl.codegen.Templates
import io.github.amzn.ridl.model.Document
import io.github.amzn.ridl.model.Name
import io.github.amzn.ridl.model.Namespace
import io.github.amzn.ridl.model.Primitive
import io.github.amzn.ridl.model.RTypeArray
import io.github.amzn.ridl.model.RTypeEnum
import io.github.amzn.ridl.model.RTypeNamed
import io.github.amzn.ridl.model.RTypePrimitive
import io.github.amzn.ridl.model.RTypeStruct
import io.github.amzn.ridl.model.RTypeUnion
import io.github.amzn.ridl.model.RTypeUnit
import io.github.amzn.ridl.model.Type
import java.io.File

/**
 * Direct conversion of RIDL types to ISL definitions; consider just building IonValues and pretty-print.
 */
internal object IslGenerator {

    private val templates = io.github.amzn.ridl.codegen.Templates("isl")

    fun generate(document: Document): List<File> {

        // Add the header which contains our primitives
        val header = templates.apply("header", Unit)
        println(header)

        document.definitions.forEach {
            val body = when (it) {
                is Namespace -> generate(it)
                is Type -> generate(it)
            }
            println(body)
        }
        return emptyList()
    }

    private fun generate(namespace: Namespace): String {
        TODO()
    }

    private fun generate(type: Type): String = when (type.type) {
        is RTypeArray -> generate(type.name, type.type)
        is RTypeEnum -> generate(type.name, type.type)
        is RTypeNamed -> generate(type.name, type.type)
        is RTypePrimitive -> generate(type.name, type.type)
        is RTypeStruct -> generate(type.name, type.type)
        is RTypeUnion -> generate(type.name, type.type)
        is RTypeUnit -> generate(type.name, type.type)
    }

    private fun generate(name: Name, type: RTypeArray): String {
        val e = type.item
        if (e is RTypePrimitive && e.kind == Primitive.BYTE) {
            val ctx = IslBlob(
                    name = name.tag(),
                    size = type.size,
            )
            return templates.apply("type_blob", ctx)
        }
        val element = when (e) {
            is RTypeNamed -> e.name.tag()
            is RTypePrimitive -> e.name
        }
        val ctx = IslArray(
                name = name.tag(),
                element = element,
                size = type.size,
        )
        return templates.apply("type_array", ctx)
    }

    private fun generate(name: Name, type: RTypeEnum): String {
        val ctx = IslEnum(
                name = name.tag(),
                values = type.values,
        )
        return templates.apply("type_enum", ctx)
    }

    private fun generate(name: Name, type: RTypeNamed): String {
        val ctx = IslAlias(
                name = name.tag(),
                type = type.name.tag(),
        )
        return templates.apply("type_alias", ctx)
    }

    private fun generate(name: Name, type: RTypePrimitive): String {
        val ctx = IslAlias(
                name = name.tag(),
                type = type.name,
        )
        return templates.apply("type_alias", ctx)
    }

    private fun generate(name: Name, type: RTypeStruct): String {
        val operands = type.fields.map {
            when (it.type) {
                is RTypeNamed -> it.type.name.tag()
                is RTypePrimitive -> it.type.name
            }
        }
        val ctx = IslSexp(
                name = name.tag(),
                size = type.fields.size,
                operands = operands,
        )
        return templates.apply("type_struct", ctx)
    }

    private fun generate(name: Name, type: RTypeUnion): String {
        val variants = type.variants.map {
            val vName = it.name.tag()
            val vType = generate(Type(it.name, it.type))
            IslVariant(vName, vType)
        }
        val ctx = IslOneOf(
                name = name.tag(),
                variants = variants,
        )
        return templates.apply("type_union", ctx)
    }

    private fun generate(name: Name, type: RTypeUnit): String {
        val ctx = IslUnit(
                name = name.tag(),
        )
        return templates.apply("type_unit", ctx)
    }

    private fun Name.tag(): String = path.joinToString("::") { it }
}
