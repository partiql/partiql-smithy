package org.partiql.tool.ridl.codegen.language.kotlin

import net.pearx.kasechange.toPascalCase
import org.partiql.tool.ridl.codegen.Generator
import org.partiql.tool.ridl.codegen.Templates
import org.partiql.tool.ridl.model.Document
import org.partiql.tool.ridl.model.Name
import org.partiql.tool.ridl.model.Namespace
import org.partiql.tool.ridl.model.Primitive
import org.partiql.tool.ridl.model.RTypeArray
import org.partiql.tool.ridl.model.RTypeEnum
import org.partiql.tool.ridl.model.RTypeNamed
import org.partiql.tool.ridl.model.RTypePrimitive
import org.partiql.tool.ridl.model.RTypeRef
import org.partiql.tool.ridl.model.RTypeStruct
import org.partiql.tool.ridl.model.RTypeUnion
import org.partiql.tool.ridl.model.RTypeUnit
import org.partiql.tool.ridl.model.Type
import java.io.File

internal class KotlinGenerator(
    private val packageName: String,
) : Generator {

    private val templates = Templates("kotlin")

    override fun generate(document: Document): List<File> {
        val root = KotlinPackage(
            name = packageName,
            files = mutableListOf(),
            packages = mutableListOf(),
        )
        generateModel(document)
        generateVisitors(document)
        generateBuilders(document)
        return emptyList()
    }

    private fun generateModel(document: Document) {
        val file = KotlinFile(
            `package` = packageName,
            name = "Model",
            imports = mutableSetOf()
        )
        
    }

    private fun generate(namespace: Namespace): KotlinPackage {
        TODO()
    }

    // private fun generate(file: KotlinFile, type: Type): String = when (type.type) {
    //     is RTypeArray -> generate(type.name, type.type)
    //     is RTypeEnum -> generate(type.name, type.type)
    //     is RTypeNamed -> generate(type.name, type.type)
    //     is RTypePrimitive -> generate(type.name, type.type)
    //     is RTypeStruct -> generate(type.name, type.type)
    //     is RTypeUnion -> generate(type.name, type.type)
    //     is RTypeUnit -> generate(type.name, type.type)
    // }

    /**
     * TODO
     *  - add reference to namespace imports
     *  - serde
     */
    private fun generate(name: Name, type: RTypeArray): String {
        val ctx = KAlias(
            simplename = name.simplename(),
            type = "Array<${type.type.reference()}>",
        )
        return templates.apply("type_alias", ctx)
    }

    /**
     * TODO
     *  - serde
     */
    private fun generate(name: Name, type: RTypeEnum): String {
        val ctx = KEnum(
            classname = name.classname(),
            simplename = name.simplename(),
            enumerators = type.values,
        )
        return templates.apply("type_enum", ctx)
    }

    private fun generate(name: Name, type: RTypeNamed): String {
        val ctx = KAlias(
            simplename = name.simplename(),
            type = type.reference(),
        )
        return templates.apply("type_alias", ctx)
    }

    private fun generate(name: Name, type: RTypePrimitive): String {
        val ctx = KAlias(
            simplename = name.simplename(),
            type = type.reference(),
        )
        return templates.apply("type_alias", ctx)
    }

    private fun generate(name: Name, type: RTypeStruct): String {
        val fields = type.fields.map {
            val fName = it.name
            val fType = it.type.reference()
            KField(fName, fType)
        }
        val ctx = KStruct(
            classname = name.classname(),
            simplename = name.simplename(),
            fields = fields,
        )
        return templates.apply("type_struct", ctx)
    }

    /**
     * TODO
     *  - parent classes (if any)
     */
    private fun generate(name: Name, type: RTypeUnion): String {
        val variants = type.variants.map {
            KVariant(
                simplename = it.name.simplename(),
            )
        }
        val ctx = KUnion(
            classname = name.classname(),
            simplename = name.simplename(),
            variants = variants,
            parent = "Example",
        )
        return templates.apply("type_union", ctx)
    }

    /**
     * TODO
     *  - parent classes (if any)
     */
    private fun generate(name: Name, type: RTypeUnit): String {
        val ctx = KUnit(
            classname = name.classname(),
            simplename = name.simplename(),
        )
        return templates.apply("type_unit", ctx)
    }

    private fun Name.classname(): String = path.joinToString(".") { it.toPascalCase() }

    private fun Name.simplename(): String = name.toPascalCase()

    private fun RTypeRef.reference(): String = when (this) {
        is RTypeNamed -> name.classname()
        is RTypePrimitive -> when (kind) {
            Primitive.BOOL -> "Boolean"
            Primitive.INT32 -> "Int"
            Primitive.INT64 -> "Long"
            Primitive.FLOAT32 -> "Float"
            Primitive.FLOAT64 -> "Double"
            Primitive.STRING -> "String"
            Primitive.BYTE -> "Byte"
            Primitive.BYTES -> "ByteArray"
        }
    }
}
