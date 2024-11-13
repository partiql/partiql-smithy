package io.github.amzn.anodizer.smithy.util

import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.core.Name
import io.github.amzn.anodizer.core.Type
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

/**
 * Translate Smithy shapes to Anodizer definitions.
 */
internal class Transformer : ShapeVisitor<Definition?> {

    override fun booleanShape(shape: BooleanShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Bool
        return Definition.Alias(name, type)
    }

    override fun byteShape(shape: ByteShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Int
        return Definition.Alias(name, type)
    }

    override fun shortShape(shape: ShortShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Int
        return Definition.Alias(name, type)
    }

    override fun integerShape(shape: IntegerShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Int
        return Definition.Alias(name, type)
    }

    override fun longShape(shape: LongShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Int
        return Definition.Alias(name, type)
    }

    override fun bigIntegerShape(shape: BigIntegerShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Int
        return Definition.Alias(name, type)
    }

    override fun bigDecimalShape(shape: BigDecimalShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Decimal(null, null)
        return Definition.Alias(name, type)
    }

    override fun floatShape(shape: FloatShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Float
        return Definition.Alias(name, type)
    }

    override fun doubleShape(shape: DoubleShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Float
        return Definition.Alias(name, type)
    }

    override fun stringShape(shape: StringShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.String
        return Definition.Alias(name, type)
    }

    override fun blobShape(shape: BlobShape): Definition {
        val name = name(shape.id)
        val type = Type.Primitive.Blob(null)
        return Definition.Alias(name, type)
    }

    override fun timestampShape(shape: TimestampShape): Definition {
        throw UnsupportedOperationException("timestamp")
    }

    override fun listShape(shape: ListShape): Definition {
        throw UnsupportedOperationException("array")
    }

    //-- struct

    override fun structureShape(shape: StructureShape): Definition {
        val name = name(shape.id)
        val fields = shape.members().map { member ->
            val n = screaming(member.memberName)
            val t = resolve(member)
            Definition.Struct.Field(n, t)
        }
        return Definition.Struct(name, fields)
    }

    override fun mapShape(shape: MapShape): Definition {
        throw UnsupportedOperationException("map")
    }

    //-- union

    override fun unionShape(shape: UnionShape): Definition {
        TODO("Not yet implemented")
    }

    //-- enum

    override fun enumShape(shape: EnumShape): Definition {
        val name = name(shape.id)
        val enum = shape.members().map { screaming(it.memberName) }
        return Definition.Enum(name, enum)
    }

    override fun intEnumShape(shape: IntEnumShape?): Definition? {
        throw UnsupportedOperationException("int enum")
    }

    //-- ignored

    override fun memberShape(shape: MemberShape): Definition? {
        // ignore, silent.
        return null
    }

    override fun documentShape(shape: DocumentShape): Definition? {
        // ignore, silent.
        return null
    }

    override fun operationShape(shape: OperationShape): Definition? {
        // ignore, silent.
        return null
    }

    override fun resourceShape(shape: ResourceShape): Definition? {
        // ignore, silent.
        return null
    }

    override fun serviceShape(shape: ServiceShape): Definition? {
        // ignore, silent.
        return null
    }

    private fun name(id: ShapeId): Name {
        val name = snakecase(id.name)
        val path = arrayOf(name)
        return Name(name, path)
    }

    private fun snakecase(str: String): String {
        return str.toSnakeCase()
    }

    private fun screaming(str: String): String {
        return str.toScreamingSnakeCase()
    }

    private fun resolve(member: MemberShape): Type {
        if (member.target.namespace == "smithy.api") {
            return when (member.target.name) {
                // primitive bool
                "Boolean" -> Type.Primitive.Bool
                "Byte",
                "Short",
                "Integer",
                "Long",
                "BigInteger",
                -> Type.Primitive.Int
                "BigDecimal" -> Type.Primitive.Decimal(null, null)
                "Float",
                "Double",
                -> Type.Primitive.Float
                "String" -> Type.Primitive.String
                "Blob" -> Type.Primitive.Blob(null)
                "Timestamp" -> TODO("timestamp")
                else -> throw IllegalArgumentException("unknown primitive: ${member.target}")
            }
        }
        throw UnsupportedOperationException("inlines")
    }
}
