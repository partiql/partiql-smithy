package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Generator
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.Ion
import io.github.amzn.anodizer.core.Type

internal abstract class KotlinGenerator(
    context: Context.Domain,
    templates: Templates,
) : Generator.Base(context, templates) {

    override fun method(symbol: Context.Symbol, prefix: String?, suffix: String?): String {
        var method = symbol.path.camel
        if (prefix != null) method = prefix + symbol.path.pascal
        if (suffix != null) method += suffix
        return method
    }

    override fun pathTo(symbol: Context.Symbol): String {
        val root = context.name.pascal
        val path = symbol.path.pascal(".")
        return "$root.$path"
    }

    override fun typeOfArray(array: Context.Array): String {
        return "Collection<${typeOf(array.item)}>"
    }

    override fun typeOfNamed(named: Context.Named): String {
        return pathTo(named.symbol)
    }

    override fun typeOfPrimitive(primitive: Context.Primitive): String {
        return when (primitive.ion) {
            Ion.BOOL -> "Boolean"
            Ion.INT -> "Long"
            Ion.FLOAT -> "Double"
            Ion.DECIMAL -> "Decimal"
            Ion.STRING -> "String"
            Ion.CLOB -> "ByteArray"
            Ion.BLOB -> "ByteArray"
            else -> error("Unsupported Ion type `${primitive.ion}`")
        }
    }

    override fun typeOfUnit(unit: Context.Unit): String {
        return "IonUnit"
    }

    /**
     * Keep??
     */
    internal fun Context.Primitive.constructor(): String {
        val args: MutableList<String> = mutableListOf("value")
        val constructor = when (val type = type) {
            is Type.Primitive.Void -> TODO("void type not supported")
            is Type.Primitive.Bool -> "IonBool"
            is Type.Primitive.Int -> "IonInt"
            is Type.Primitive.Decimal -> {
                if (type.precision != null) args.add(type.precision.toString())
                if (type.exponent != null) args.add(type.exponent.toString())
                "IonDecimal"
            }
            is Type.Primitive.Float -> "IonFloat"
            is Type.Primitive.String -> "IonString"
            is Type.Primitive.Blob -> {
                if (type.size != null) args.add(type.size.toString())
                "IonBlob"
            }
            is Type.Primitive.Clob -> {
                if (type.size != null) args.add(type.size.toString())
                "IonClob"
            }
        }
        return "${constructor}(${args.joinToString()})"
    }
}
