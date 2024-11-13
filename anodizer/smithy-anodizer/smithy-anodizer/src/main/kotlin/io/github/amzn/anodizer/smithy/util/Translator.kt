package io.github.amzn.anodizer.smithy.util

import net.pearx.kasechange.toScreamingSnakeCase
import net.pearx.kasechange.toSnakeCase
import software.amazon.smithy.model.shapes.BigDecimalShape
import software.amazon.smithy.model.shapes.BigIntegerShape
import software.amazon.smithy.model.shapes.BlobShape
import software.amazon.smithy.model.shapes.BooleanShape
import software.amazon.smithy.model.shapes.ByteShape
import software.amazon.smithy.model.shapes.DocumentShape
import software.amazon.smithy.model.shapes.DoubleShape
import software.amazon.smithy.model.shapes.EnumShape
import software.amazon.smithy.model.shapes.FloatShape
import software.amazon.smithy.model.shapes.IntEnumShape
import software.amazon.smithy.model.shapes.IntegerShape
import software.amazon.smithy.model.shapes.ListShape
import software.amazon.smithy.model.shapes.LongShape
import software.amazon.smithy.model.shapes.MapShape
import software.amazon.smithy.model.shapes.MemberShape
import software.amazon.smithy.model.shapes.OperationShape
import software.amazon.smithy.model.shapes.ResourceShape
import software.amazon.smithy.model.shapes.ServiceShape
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.shapes.ShapeVisitor
import software.amazon.smithy.model.shapes.ShortShape
import software.amazon.smithy.model.shapes.StringShape
import software.amazon.smithy.model.shapes.StructureShape
import software.amazon.smithy.model.shapes.TimestampShape
import software.amazon.smithy.model.shapes.UnionShape
import java.io.PrintStream

/**
 * Produce anodizer definitions from the smithy shapes.
 */
internal class Translator(private val out: PrintStream) : ShapeVisitor<Unit> {

    private var i = 0

    override fun booleanShape(shape: BooleanShape) {
        val name = name(shape.id)
        write("type $name bool;")
    }

    override fun byteShape(shape: ByteShape) {
        val name = name(shape.id)
        write("type $name int;")
    }

    override fun shortShape(shape: ShortShape) {
        val name = name(shape.id)
        write("type $name int;")
    }

    override fun integerShape(shape: IntegerShape) {
        val name = name(shape.id)
        write("type $name int;")
    }

    override fun longShape(shape: LongShape) {
        val name = name(shape.id)
        write("type $name int;")
    }

    override fun bigIntegerShape(shape: BigIntegerShape) {
        val name = name(shape.id)
        write("type $name int;")
    }

    override fun bigDecimalShape(shape: BigDecimalShape) {
        val name = name(shape.id)
        write("type $name decimal;")
    }

    override fun floatShape(shape: FloatShape) {
        val name = name(shape.id)
        write("type $name double;")
    }

    override fun doubleShape(shape: DoubleShape) {
        val name = name(shape.id)
        write("type $name double;")
    }

    override fun stringShape(shape: StringShape) {
        val name = name(shape.id)
        write("type $name string;")
    }

    override fun blobShape(shape: BlobShape) {
        val name = name(shape.id)
        write("type $name blob;")
    }

    override fun timestampShape(shape: TimestampShape) {
        val name = name(shape.id)
        write("type $name timestamp;")
    }

    override fun listShape(shape: ListShape) {
        throw UnsupportedOperationException("array")
    }

    //-- struct

    override fun structureShape(shape: StructureShape) {

        val name = name(shape.id)
        val fields = shape.members().map { member ->
            val k = snakecase(member.memberName)
            val t = resolve(member.target)
            Pair(k, t)
        }

        //
        // type <name> struct { ... }
        //

        stepIn("type $name struct {")
        for ((k, t) in fields) {
            writeln()
            write("$k: $t,")
        }
        if (fields.isNotEmpty()) {
            writeln()
        }
        stepOut("};")
    }

    override fun mapShape(shape: MapShape) {
        throw UnsupportedOperationException("map")
    }

    //-- union

    override fun unionShape(shape: UnionShape) {
        throw UnsupportedOperationException("union")
    }

    //-- enum

    override fun enumShape(shape: EnumShape) {

        val name = name(shape.id)
        val enum = shape.members().map { screaming(it.memberName) }

        //
        // type <name> enum { ... }
        //

        stepIn("type $name enum {")
        for (e in enum) {
            writeln()
            write("$e,")
        }
        if (enum.isNotEmpty()) {
            writeln()
        }
        stepOut("};")
    }

    override fun intEnumShape(shape: IntEnumShape) {
        throw UnsupportedOperationException("int enum")
    }

    //-- ignored

    override fun memberShape(shape: MemberShape) {
        // ignore, silent.
    }

    override fun documentShape(shape: DocumentShape) {
        // ignore, silent.
    }

    override fun operationShape(shape: OperationShape) {
        // ignore, silent.
    }

    override fun resourceShape(shape: ResourceShape) {
        // ignore, silent.
    }

    override fun serviceShape(shape: ServiceShape) {
        // ignore, silent.
    }

    //-- helpers

    private fun stepIn(str: String? = null) {
        if (str != null) {
            write(str)
        }
        i += 1
    }

    private fun stepOut(str: String? = null) {
        i -= 1
        if (str != null) {
            write(str)
        }
    }

    private fun writeln() {
        out.println()
    }

    private fun write(str: String) {
        if (i > 0) {
            out.print("  ".repeat(i))
        }
        out.print(str)
    }

    private fun name(id: ShapeId): String {
        return snakecase(id.name)
    }

    private fun snakecase(str: String): String {
        return str.toSnakeCase()
    }

    private fun screaming(str: String): String {
        return str.toScreamingSnakeCase()
    }

    private fun resolve(id: ShapeId): String {
        if (id.namespace == "smithy.api") {
            return when (id.name) {
                // primitive bool
                "Boolean" -> "bool"
                "Byte",
                "Short",
                "Integer",
                "Long",
                "BigInteger",
                -> "int"
                "BigDecimal" -> "decimal"
                "Float",
                "Double",
                -> "float"
                "String" -> "string"
                "Blob" -> "blob"
                "Timestamp" -> "timestamp"
                else -> throw IllegalArgumentException("unknown primitive: $id")
            }
        }
        return name(id)
    }
}
