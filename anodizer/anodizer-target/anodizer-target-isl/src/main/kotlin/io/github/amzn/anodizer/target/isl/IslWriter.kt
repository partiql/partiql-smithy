package io.github.amzn.anodizer.target.isl

import com.amazon.ion.system.IonTextWriterBuilder
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.StructElement
import com.amazon.ionelement.api.StructField
import com.amazon.ionelement.api.SymbolElement
import com.amazon.ionelement.api.field
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionSymbol
import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.core.Definition.Alias
import io.github.amzn.anodizer.core.Definition.Enum
import io.github.amzn.anodizer.core.Definition.Namespace
import io.github.amzn.anodizer.core.Definition.Struct
import io.github.amzn.anodizer.core.Definition.Union
import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.core.Encoding
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Type

internal class IslWriter private constructor(
    private val file: File,
    private val encoding: Encoding,
) {

    companion object {

        @JvmStatic
        fun text(file: File):  IslWriter = IslWriter(file, Encoding.TEXT)

        @JvmStatic
        fun packed(file: File):  IslWriter = IslWriter(file, Encoding.PACKED)

        val UNIT: StructElement = ionStructOf(
            "name" to ionSymbol("unit"),
            "type" to ionSymbol("symbol"),
            "valid_values" to ionListOf(ionSymbol("unit")),
        ).withAnnotations("type")
    }

    fun write(model: AnodizerModel) {
        // Header
        file.write(ionSymbol("\$ion_schema_2_0"))
        file.writeln()
        file.write(UNIT)
        file.writeln()
        // Definitions
        for (definition in model.definitions) {
            writeDefinition(definition)
            file.writeln()
        }
    }

    private fun writeDefinition(definition: Definition): Unit = when (definition) {
        is Namespace -> writeNamespace(definition)
        is Alias -> writeAlias(definition)
        is Enum -> writeEnum(definition)
        is Struct -> writeStruct(definition)
        is Union -> writeUnion(definition)
    }

    private fun writeNamespace(namespace: Namespace) {
        for (definition in namespace.definitions) {
            writeDefinition(definition)
        }
    }

    private fun writeAlias(alias: Alias) {
        val name = alias.name.tag
        val type = alias.type.arg()
        val constraints = IslConstraints()
        when (type) {
            is StructElement -> constraints.addAll(type.fields)
            is SymbolElement -> constraints.type(type)
            else -> error("Expected a named type argument or an inline type argument.")
        }
        define(name, constraints)
    }

    private fun writeEnum(enum: Enum) {
        val name = enum.name.tag
        val values = enum.values.map { ionSymbol(it) }
        val constraints = IslConstraints()
            .type(ionSymbol("symbol"))
            .validValues(values)
        define(name, constraints)
    }

    private fun writeStruct(struct: Struct) = when (encoding) {
        Encoding.TEXT -> writeStructText(struct)
        Encoding.PACKED -> writeStructPacked(struct)
    }

    private fun writeStructText(struct: Struct) {
        val name = struct.name.tag
        val fields = struct.fields.map {
            val k = it.name
            val v = it.type.arg()
            field(k, v)
        }
        val constraints = IslConstraints()
            .type(ionSymbol("struct"))
            .fields(fields)
        define(name, constraints)
    }

    private fun writeStructPacked(struct: Struct) {
        val name = struct.name.tag
        val orderedElements = struct.fields.map { it.type.arg() }
        val constraints = IslConstraints()
            .type(ionSymbol("sexp"))
            .containerLength(orderedElements.size)
            .orderedElements(orderedElements)
        define(name, constraints)
    }

    private fun writeUnion(union: Union) = when (encoding) {
        Encoding.TEXT -> writeUnionText(union)
        Encoding.PACKED -> writeUnionPacked(union)
    }

    private fun writeUnionText(union: Union) {
        // Define all variants
        for (variant in union.variants) {
            writeDefinition(variant)
            file.writeln()
        }
        // Define the union itself
        val name = union.name.tag
        val oneOf = union.variants.map {
            // TODO fix modeling of definitions
            val name = when (it) {
                is Alias -> it.name
                is Enum -> it.name
                is Struct -> it.name
                is Union -> it.name
            }
            ionSymbol(name.tag)
        }
        val constraints = IslConstraints().oneOf(oneOf)
        define(name, constraints, tag = false)
    }

    private fun writeUnionPacked(union: Union) {
        // Define all variants
        for (variant in union.variants) {
            writeDefinition(variant)
            file.writeln()
        }
        // Define the union itself
        val name = union.name.tag
        val tag = ionStructOf(
            "type" to ionSymbol("int"),
            "valid_values" to ionListOf(ionInt(0L), ionInt(union.variants.size - 1L)).withAnnotations("range")
        )
        val oneOf = ionStructOf(
            "one_of" to ionListOf(union.variants.map {
                // TODO fix modeling of definitions
                val name = when (it) {
                    is Alias -> it.name
                    is Enum -> it.name
                    is Struct -> it.name
                    is Union -> it.name
                }
                ionSymbol(name.tag)
            })
        )
        val orderedElements = listOf(tag, oneOf)
        val constraints = IslConstraints()
            .type(ionSymbol("sexp"))
            .containerLength(2)
            .orderedElements(orderedElements)
        define(name, constraints, tag = false)
    }

    private fun Type.arg(): IonElement = when (this) {
        is Type.Array -> {
            val constraints = IslConstraints()
            constraints.type(ionSymbol("list"))
            constraints.element(item.arg())
            if (size != null) constraints.containerLength(size!!)
            ionStructOf(constraints.build())
        }
        is Type.Named -> ionSymbol(name.tag)
        is Type.Primitive.Blob -> {
            val constraints = IslConstraints()
            if (size != null) constraints.byteLength(size!!)
            when (constraints.isEmpty()) {
                true -> ionSymbol("blob")
                else -> inline("blob", constraints)
            }
        }
        is Type.Primitive.Bool -> ionSymbol("bool")
        is Type.Primitive.Clob -> {
            val constraints = IslConstraints()
            if (size != null) constraints.byteLength(size!!)
            when (constraints.isEmpty()) {
                true -> ionSymbol("clob")
                else -> inline("clob", constraints)
            }
        }
        is Type.Primitive.Decimal -> {
            val constraints = IslConstraints()
            if (precision != null) constraints.precision(precision!!)
            if (exponent != null) constraints.exponent(exponent!!)
            when (constraints.isEmpty()) {
                true -> ionSymbol("decimal")
                else -> inline("decimal", constraints)
            }
        }
        is Type.Primitive.Float -> ionSymbol("float")
        is Type.Primitive.Int -> ionSymbol("int")
        is Type.Primitive.String -> ionSymbol("string")
        is Type.Primitive.Void -> ionSymbol("void")
        is Type.Unit -> ionSymbol("unit")
    }

    //--- Helpers -------------------------------------------------------------

    private fun File.write(ion: IonElement) {
        val sb = StringBuilder()
        val writer = IonTextWriterBuilder.pretty().build(sb)
        ion.writeTo(writer)
        write(sb.toString())
    }

    private fun define(name: String, constraints: IslConstraints, tag: Boolean = true) {
        val fields = IslConstraints()
        fields.name(name)
        if (tag && encoding == Encoding.TEXT) {
            // add tag annotations for every type definition
            fields.annotation(name, required = true)
        }
        fields.addAll(constraints.build())
        file.write(ionStructOf(fields, annotations = listOf("type")))
    }

    private fun inline(type: String, constraints: IslConstraints): IonElement {
        val fields = mutableListOf<StructField>()
        fields.add(field("type", ionSymbol(type)))
        fields.addAll(constraints.build())
        return ionStructOf(fields)
    }
}
