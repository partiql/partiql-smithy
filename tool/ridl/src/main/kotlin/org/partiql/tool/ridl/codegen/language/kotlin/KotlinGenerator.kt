package org.partiql.tool.ridl.codegen.language.kotlin

import com.amazon.ion.IonType
import net.pearx.kasechange.toPascalCase
import org.partiql.tool.ridl.codegen.Templates
import org.partiql.tool.ridl.codegen.language.kotlin.KotlinGenerator.ion
import org.partiql.tool.ridl.model.*
import java.io.File

internal object KotlinGenerator {

    private val templates = Templates("kotlin")

    fun generate(options: KotlinOptions, document: Document): List<File> {

        // Convert RIDL document to a Kotlin model
        val model = document.toKModel(options)

        //-------------------------------------------------------------------------
        // BEGIN: package
        //-------------------------------------------------------------------------
        // ./
        val root = KotlinPackage(options.pkg)
        // ./$namespace.kt
        root.mkfile("${options.namespace}.kt", "file_types", model)
        //-------------------------------------------------------------------------
        // END: package
        //-------------------------------------------------------------------------

        // For now just println
        root.write(templates)

        return emptyList()
    }

    // TODO namespaces
    private fun Document.toKModel(options: KotlinOptions) = KModel(
        `package` = options.pkg.joinToString("."),
        namespace = KNamespace(
            name = options.namespace.toPascalCase(),
            types = definitions.filterIsInstance<Type>().map { it.toKType() }
        )
    )

    private fun Type.toKType(parent: String = "IonSerializable"): KType = when (type) {
        is RTypeArray -> type.toKType(name, parent)
        is RTypeEnum -> type.toKType(name, parent)
        is RTypeNamed -> type.toKType(name, parent)
        is RTypePrimitive -> type.toKType(name, parent)
        is RTypeStruct -> type.toKType(name, parent)
        is RTypeUnion -> type.toKType(name, parent)
        is RTypeUnit -> type.toKType(name, parent)
    }

    private fun RTypeArray.toKType(name: Name, parent: String) = KType(
        array = KArray(
            path = name.path(),
            name = name.name(),
            item = item.reference(),
            itemIon = item.ion(),
            size = size,
            write = item.write("item"),
            read = item.read(),
        )
    )

    private fun RTypeEnum.toKType(name: Name, parent: String) = KType(
        enum = KEnum(
            path = name.path(),
            name = name.name(),
            values = values,
        )
    )

    private fun RTypeNamed.toKType(name: Name, parent: String): KType = TODO()

    private fun RTypePrimitive.toKType(name: Name, parent: String) = KType(
        scalar = KScalar(
            path = name.path(),
            name = name.name(),
            parent = parent,
            type = reference(),
            write = write("value")!!,
            read = read()!!
        )
    )

    private fun RTypeStruct.toKType(name: Name, parent: String) = KType(
        struct = KStruct(
            path = name.path(),
            name = name.name(),
            parent = parent,
            fields = fields.map {
                val fName = it.name
                val fType = it.type.reference()
                val fIon = it.type.ion()
                val fWrite = it.type.write(fName)
                val fRead = it.type.read()
                KField(fName, fType, fIon, fWrite, fRead)
            },
            builder = name.builder(),
            visit = name.visit(),
        )
    )

    private fun RTypeUnion.toKType(name: Name, parent: String) = KType(
        union = KUnion(
            path = name.path(),
            name = name.name(),
            parent = parent,
            variants = variants.mapIndexed { i, v ->
                KVariant(
                    tag = i,
                    name = v.name.name(),
                    type = v.toKType(name.name())
                )
            },
            builder = name.builder(),
            visit = name.visit(),
        )
    )

    private fun RTypeUnit.toKType(name: Name, parent: String) = KType(
        unit = KUnit(
            path = name.path(),
            name = name.name(),
            parent = parent,
            tag = name.path(),
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

    private fun RTypeRef.write(arg: String): String? = when (this) {
        is RTypeNamed -> null
        is RTypePrimitive -> when (kind) {
            Primitive.BOOL -> "writeBool($arg)"
            Primitive.INT32 -> "writeInt($arg.toLong())"
            Primitive.INT64 -> "writeInt($arg)"
            Primitive.FLOAT32 -> "writeFloat($arg.toDouble())"
            Primitive.FLOAT64 -> "writeFloat($arg)"
            Primitive.STRING -> "writeString($arg)"
            Primitive.BYTE -> "writeBlob(byteArrayOf($arg))"
            Primitive.BYTES -> "writeBlob($arg)"
        }
    }

    private fun RTypeRef.read(): String? = when (this) {
        is RTypeNamed -> null
        is RTypePrimitive -> when (kind) {
            Primitive.BOOL -> "booleanValue()"
            Primitive.INT32 -> "intValue()"
            Primitive.INT64 -> "longValue()"
            Primitive.FLOAT32 -> "doubleValue().toFloat()"
            Primitive.FLOAT64 -> "doubleValue()"
            Primitive.STRING -> "stringValue()"
            Primitive.BYTE -> "newBytes()[0]"
            Primitive.BYTES -> "newBytes()"
        }
    }

    private fun Name.path(): String = path.joinToString(".") { it.toPascalCase() }

    private fun Name.name(): String = name.toPascalCase()

    private fun Name.pascal(): String = path.joinToString("") { it.toPascalCase() }

    private fun Name.builder(): String = "${pascal()}Builder"

    private fun Name.visit(): String = "visit${pascal()}"

    private fun RType.ion(): IonType = when (this) {
        is RTypeNamed -> IonType.SEXP
        is RTypeArray -> IonType.LIST
        is RTypeEnum -> IonType.SYMBOL
        is RTypeStruct -> IonType.SEXP
        is RTypeUnion -> IonType.SEXP
        is RTypeUnit -> IonType.SYMBOL
        is RTypePrimitive -> when (kind) {
            Primitive.BOOL -> IonType.BOOL
            Primitive.INT32 -> IonType.INT
            Primitive.INT64 -> IonType.INT
            Primitive.FLOAT32 -> IonType.FLOAT
            Primitive.FLOAT64 -> IonType.FLOAT
            Primitive.STRING -> IonType.STRING
            Primitive.BYTE -> IonType.BLOB
            Primitive.BYTES -> IonType.BLOB
        }
    }
}
