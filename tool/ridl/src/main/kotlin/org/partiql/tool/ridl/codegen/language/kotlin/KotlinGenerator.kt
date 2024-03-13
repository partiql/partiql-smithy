package org.partiql.tool.ridl.codegen.language.kotlin

import net.pearx.kasechange.toPascalCase
import org.partiql.tool.ridl.codegen.Generator
import org.partiql.tool.ridl.codegen.Templates
import org.partiql.tool.ridl.model.Document
import org.partiql.tool.ridl.model.Name
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
    private val `package`: Array<String>,
) : Generator {

    private val templates = Templates("kotlin")

    override fun generate(document: Document): List<File> {
        val model = document.toKModel()

        //-------------------------------------------------------------------------
        // START: Kotlin Package Structure
        //-------------------------------------------------------------------------
        // ./
        val root = KotlinPackage(`package`)
        // ./Model.kt
        root.mkfile("Model.kt", "file_model", model)
        // ./visitors/
        val visitors = root.mkdir("visitor")
        visitors.mkfile("Rewriter", "visitor/file_factory", model)
        visitors.mkfile("Visitor", "visitor/file_builder", model)
        visitors.mkfile("VisitorBase", "visitor/file_builders", model)
        // ./builders/
        val builders = root.mkdir("builder")
        builders.mkfile("Factory.kt", "builder/file_factory", model)
        builders.mkfile("Builder.kt", "builder/file_builder", model)
        builders.mkfile("Builders.kt", "builder/file_builders", model)
        //-------------------------------------------------------------------------
        // END
        //-------------------------------------------------------------------------

        root.write()
        return emptyList()
    }

    // TODO namespaces
    private fun Document.toKModel() = KModel(
        types = definitions
            .filterIsInstance<Type>()
            .map { it.toKType() }
    )

    private fun Type.toKType(parent: String? = null): KType = when (type) {
        is RTypeArray -> type.toKType(name, parent)
        is RTypeEnum -> type.toKType(name, parent)
        is RTypeNamed -> type.toKType(name, parent)
        is RTypePrimitive -> type.toKType(name, parent)
        is RTypeStruct -> type.toKType(name, parent)
        is RTypeUnion -> TODO()
        is RTypeUnit -> type.toKType(name, parent)
    }

    private fun RTypeArray.toKType(name: Name, parent: String?) = KType(
        array = KArray(
            path = name.path(),
            name = name.name(),
            item = item.reference(),
            size = size,
        )
    )

    private fun RTypeEnum.toKType(name: Name, parent: String?) = KType(
        enum = KEnum(
            path = name.path(),
            name = name.name(),
            values = values,
        )
    )

    private fun RTypeNamed.toKType(name: Name, parent: String?) = KType(
        alias = KAlias(
            name = name.name(),
            type = reference(),
        )
    )

    private fun RTypePrimitive.toKType(name: Name, parent: String?) = KType(
        alias = KAlias(
            name = name.name(),
            type = reference(),
        )
    )

    private fun RTypeStruct.toKType(name: Name, parent: String?) = KType(
        struct = KStruct(
            path = name.path(),
            name = name.name(),
            fields = emptyList(),
            parent = parent,
            builder = name.builder(),
            visit = name.visit(),
        )
    )

    private fun RTypeUnion.toKType(name: Name, parent: String?) = KType(
        union = KUnion(
            path = name.path(),
            name = name.name(),
            variants = emptyList(),
            parent = parent,
            builder = name.builder(),
            visit = name.visit(),
        )
    )

    private fun RTypeUnit.toKType(name: Name, parent: String?) = KType(
        unit = KUnit(
            path = name.path(),
            name = name.name(),
            parent = parent,
            builder = name.builder(),
            visit = name.visit(),
        )
    )

    private fun RTypeRef.reference(): String = when (this) {
        is RTypeNamed -> name.path()
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

    private fun Name.path(): String = path.joinToString(".") { it.toPascalCase() }

    private fun Name.name(): String = name.toPascalCase()

    private fun Name.pascal(): String = path.joinToString("") { it.toPascalCase() }

    private fun Name.builder(): String = "${pascal()}Builder"

    private fun Name.visit(): String = "visit${pascal()}"
}

