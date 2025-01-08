package io.github.amzn.anodizer.target.rust

import com.amazon.ion.IonType
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Generator
import io.github.amzn.anodizer.codegen.Templates

/**
 * Base class for all Rust generators.
 *
 * Notes // TODOs
 *  - Optimization: don't use references for type aliases to primitives.
 */
internal abstract class RustGenerator(domain: Context.Domain, templates: Templates) :
    Generator.Base(domain, templates) {

    val lifetimes: Lifetimes = Lifetimes.of(domain, index)

    override fun method(symbol: Context.Symbol, prefix: String?, suffix: String?): String {
        var m = symbol.path.snake
        if (prefix != null) m = prefix + "_" + m
        if (suffix != null) m = m + "_" + suffix
        return m
    }

    /**
     * !! IMPORTANT !! we do not join on '::' because all types are flattened.
     */
    override fun pathTo(symbol: Context.Symbol): String = when (lifetimes.contains(symbol)) {
        true -> symbol.path.pascal + "<'a>"
        else -> symbol.path.pascal
    }

    override fun typeOfArray(array: Context.Array): String = "Vec<${typeOf(array.item)}>"

    override fun typeOfPrimitive(primitive: Context.Primitive): String {
        return when (primitive.ion) {
            IonType.BOOL -> "bool" // read_bool
            IonType.INT -> "i64" // read_i64
            IonType.FLOAT -> "f64" // read_f64
            IonType.DECIMAL -> "Decimal" // read_decimal
            IonType.STRING -> "String" // read_string
            IonType.CLOB -> "Clob" // read_clob
            IonType.BLOB -> "Blob" // read_blob
            else -> error("Unsupported Ion type `${primitive.ion}`")
        }
    }

    override fun typeOfUnit(unit: Context.Unit): String = "()"
}
