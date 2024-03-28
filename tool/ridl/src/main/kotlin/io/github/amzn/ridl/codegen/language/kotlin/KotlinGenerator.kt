package io.github.amzn.ridl.codegen.language.kotlin

import com.amazon.ion.IonType
import net.pearx.kasechange.toPascalCase
import io.github.amzn.ridl.codegen.Templates
import io.github.amzn.ridl.codegen.language.kotlin.KotlinGenerator.visit
import io.github.amzn.ridl.model.*
import java.io.File

internal object KotlinGenerator {

    private val templates = io.github.amzn.ridl.codegen.Templates("kotlin")

    fun generate(options: KotlinOptions, document: Document): List<File> {

        // Convert RIDL document to a Kotlin model
        val model = document.lower().toKModel(options)

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

    private fun Document.toKModel(options: KotlinOptions): KModel {
        return KModel(
            `package` = options.pkg.joinToString("."), namespace = KNamespace(
                name = options.namespace.toPascalCase(),
                definitions = definitions.map { it.toKDefinition() },
            )
        )
    }

    private fun Definition.toKDefinition(): KDefinition = when (this) {
        is Namespace -> KDefinition(namespace = toKNamespace())
        is Type -> KDefinition(type = toKType())
    }

    private fun Namespace.toKNamespace() = KNamespace(
        name = name.name(),
        definitions = definitions.map { it.toKDefinition() },
    )

    private fun Type.toKType(parent: String = "IonSerializable", tag: Int? = null): KType = when (type) {
        is RTypeArray -> type.toKType(name, parent, tag)
        is RTypeEnum -> type.toKType(name, parent, tag)
        is RTypeNamed -> error("Cannot define an alias; model should have been lowered.")
        is RTypePrimitive -> error("Cannot define a primitive; model should have been lowered.")
        is RTypeStruct -> type.toKType(name, parent, tag)
        is RTypeUnion -> type.toKType(name, parent, tag)
        is RTypeUnit -> type.toKType(name, parent, tag)
    }

    private fun RTypeArray.toKType(name: Name, parent: String, tag: Int? = null) = KType(
        array = KArray(
            path = name.path(),
            name = name.name(),
            parent = parent,
            item = item.reference(),
            itemIon = item.ion(),
            size = size,
            write = item.write("item"),
            read = item.read(),
            tag = tag,
        )
    )

    private fun RTypeEnum.toKType(name: Name, parent: String, tag: Int? = null) = KType(
        enum = KEnum(
            path = name.path(),
            name = name.name(),
            parent = parent,
            values = values,
            tag = tag,
        )
    )

    private fun RTypeStruct.toKType(name: Name, parent: String, tag: Int? = null) = KType(
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
            tag = tag,
        )
    )

    private fun RTypeUnion.toKType(name: Name, parent: String, tag: Int? = null) = KType(
        union = KUnion(
            path = name.path(),
            name = name.name(),
            parent = parent,
            variants = variants.mapIndexed { i, v -> v.toKVariant(i, name.name()) },
            tag = tag,
        )
    )

    private fun Type.toKVariant(tag: Int, parent: String) = KVariant(
        tag = tag,
        name = name.name(),
        type = when (type) {
            is RTypeRef -> type.box(name, parent, tag)
            else -> toKType(parent, tag)
        }
    )

    /**
     * Box a reference to `T` with a simple `class Name(val value: T)` for modeling of unions with sealed classes.
     */
    private fun RTypeRef.box(name: Name, parent: String, tag: Int? = null): KType = KType(
        box = KBox(
            path = name.path(),
            name = name.name(),
            parent = parent,
            type = reference(),
            ion = ion(),
            write = write("value"),
            read = read(),
            tag = tag,
        )
    )

    private fun RTypeUnit.toKType(name: Name, parent: String, tag: Int? = null) = KType(
        unit = KUnit(
            path = name.path(),
            name = name.name(),
            parent = parent,
            tag = name.path(),
        )
    )

    private fun RTypeRef.reference(): String = when (this) {
        is RTypeNamed -> when (base) {
            null -> name.path()
            else -> base.reference()
        }

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
        is RTypeNamed -> when (base) {
            null -> null
            else -> base.write(arg)
        }

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
        is RTypeNamed -> when (base) {
            null -> null
            else -> base.read()
        }

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
