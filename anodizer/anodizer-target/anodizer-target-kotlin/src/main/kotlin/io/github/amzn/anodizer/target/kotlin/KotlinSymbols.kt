package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.codegen.Symbols
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxSymbol
import io.github.amzn.anodizer.codegen.context.CtxUnit
import io.github.amzn.anodizer.core.Ion

/**
 * The default Kotlin symbols implementation.
 */
public abstract class KotlinSymbols(model: AnodizerModel) : Symbols(model) {

    public companion object {

        /**
         * The standard Kotlin symbols implementation.
         */
        @JvmStatic
        public fun standard(model: AnodizerModel): KotlinSymbols = object : KotlinSymbols(model) {}
    }

    override fun method(symbol: CtxSymbol, prefix: String?, suffix: String?): String {
        var method = symbol.path.camel
        if (prefix != null) method = prefix + symbol.path.pascal
        if (suffix != null) method += suffix
        return method
    }

    override fun pathTo(symbol: CtxSymbol): String {
        val root = model.domain.pascal
        val path = symbol.path.pascal(".")
        return "$root.$path"
    }

    override fun typeOfArray(array: CtxArray): String {
        return "Collection<${typeOf(array.item)}>"
    }

    override fun typeOfNamed(named: CtxNamed): String {
        return pathTo(named.symbol)
    }

    override fun typeOfPrimitive(primitive: CtxPrimitive): String {
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

    override fun typeOfUnit(unit: CtxUnit): String = "IonUnit"
}
