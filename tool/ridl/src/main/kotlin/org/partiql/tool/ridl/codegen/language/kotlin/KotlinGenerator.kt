package org.partiql.tool.ridl.codegen.language.kotlin

import net.pearx.kasechange.toPascalCase
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

internal class KotlinGenerator(
    private val options: KotlinOptions,
) : Generator {

    private val templates = TemplateManager("kotlin")

    override fun generate(document: Document): List<File> {
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
        TODO()
    }

    private fun generate(name: Name, type: RTypeEnum): String {
        val ctx = KEnum(
            classname = name.classname(),
            simplename = name.simplename(),
            enumerators = type.values.map { KEnumerator(it) },
        )
        return templates.apply("type_enum", ctx)
    }

    private fun generate(name: Name, type: RTypeNamed): String {
        TODO()
    }

    private fun generate(name: Name, type: RTypePrimitive): String {
        TODO()
    }

    private fun generate(name: Name, type: RTypeStruct): String {
        TODO()
    }

    private fun generate(name: Name, type: RTypeUnion): String {
        TODO()
    }

    private fun generate(name: Name, type: RTypeUnit): String {
        TODO()
    }

    private fun Name.classname(): String = path.joinToString(".") { it.toPascalCase() }

    private fun Name.simplename(): String = name.toPascalCase()
}
