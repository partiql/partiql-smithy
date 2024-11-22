package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.codegen.context.Ctx
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxSymbol
import io.github.amzn.anodizer.codegen.context.CtxType
import io.github.amzn.anodizer.codegen.context.CtxTypedef
import io.github.amzn.anodizer.codegen.context.CtxUnit

/**
 * This controls the symbols used by a generator.
 */
public abstract class Symbols(model: AnodizerModel) {

    @JvmField
    public val model: CtxModel = Ctx.build(model)

    /**
     * For definition lookups.
     */
    private val index: Index = Index.build(this.model)

    /**
     * Return the type definition for this symbol.
     */
    public fun definitionOf(symbol: CtxSymbol): CtxTypedef = index.get(symbol.tag)

    /**
     * Return a method name for the symbol with a prefix (if any) and suffix (if any).
     */
    public abstract fun method(symbol: CtxSymbol, prefix: String? = null, suffix: String? = null): String

    /**
     * Return a reference (or null) given a symbol.
     */
    public open fun pathToOrNull(symbol: CtxSymbol?): String? {
        return if (symbol != null) pathTo(symbol) else null
    }

    /**
     * Return a reference given a symbol.
     */
    public abstract fun pathTo(symbol: CtxSymbol): String

    /**
     * Return a type reference given a type definition.
     */
    public open fun typeOf(typedef: CtxTypedef): String {
        val symbol = typedef.symbol()
        return pathTo(symbol)
    }

    /**
     * Return a type reference given a type argument.
     */
    public open fun typeOf(type: CtxType): String = when (type) {
        is CtxArray -> typeOfArray(type)
        is CtxNamed -> typeOfNamed(type)
        is CtxPrimitive -> typeOfPrimitive(type)
        is CtxUnit -> typeOf(type)
    }

    public abstract fun typeOfArray(array: CtxArray): String

    public open fun typeOfNamed(named: CtxNamed): String = pathTo(named.symbol)

    public abstract fun typeOfPrimitive(primitive: CtxPrimitive): String

    public abstract fun typeOfUnit(unit: CtxUnit): String
}
