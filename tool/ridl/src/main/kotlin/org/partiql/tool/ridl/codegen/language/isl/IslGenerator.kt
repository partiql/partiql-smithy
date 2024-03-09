package org.partiql.tool.ridl.codegen.language.isl

import org.partiql.tool.ridl.codegen.Generator
import org.partiql.tool.ridl.codegen.templating.TemplateManager
import org.partiql.tool.ridl.model.Document
import org.partiql.tool.ridl.model.Name
import org.partiql.tool.ridl.model.Namespace
import org.partiql.tool.ridl.model.RTypeArray
import org.partiql.tool.ridl.model.RTypeEnum
import org.partiql.tool.ridl.model.RTypeNamed
import org.partiql.tool.ridl.model.RTypePrimitive
import org.partiql.tool.ridl.model.RTypeStruct
import org.partiql.tool.ridl.model.RTypeUnion
import org.partiql.tool.ridl.model.RTypeUnit
import org.partiql.tool.ridl.model.Type
import java.io.File

/**
 * Direct conversion of RIDL types to ISL definitions; consider just building IonValues and pretty-print.
 */
internal class IslGenerator : Generator {

    private val templates = TemplateManager("isl")

    override fun generate(document: Document): List<File> {

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
        val e = type.type
        if (e is RTypePrimitive && e.kind == RTypePrimitive.Kind.BYTE) {
            val ctx = IslBlob(
                name = name.tag(),
                size = type.size,
            )
            return templates.apply("type_blob", ctx)
        }
        val element = when (e) {
            is RTypeNamed -> e.name.tag()
            is RTypePrimitive -> e.kind.name.lowercase()
            else -> error("Expected array of either a primitive or named type")
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
            type = type.kind.name.lowercase(),
        )
        return templates.apply("type_alias", ctx)
    }

    // TODO merged named and primitive as "reference" types.
    private fun generate(name: Name, type: RTypeStruct): String {
        val operands = type.fields.map {
            when (it.type) {
                is RTypeNamed -> it.type.name.tag()
                is RTypePrimitive -> it.type.kind.name.lowercase()
                else -> error("Currently do not support inline definitions in structs")
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
