package io.github.amzn.anodizer.codegen.context

import io.github.amzn.anodizer.core.Definition

/**
 * A contextualized type definition for code generation.
 */
public sealed interface CtxTypedef : CtxDefinition {

    /**
     * The original definition.
     */
    public fun definition(): Definition.Typedef

}

public class CtxAlias(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val type: CtxType,
    @JvmField public val parent: CtxSymbol?,
    @JvmField public val definition: Definition.Alias,
) : CtxTypedef {
    override fun definition(): Definition.Alias = definition
    override fun symbol(): CtxSymbol = symbol
    override fun children(): List<CtxDefinition> = emptyList()
    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitAlias(this, args)
}

public class CtxEnum(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val values: List<CtxName>,
    @JvmField public val parent: CtxSymbol?,
    @JvmField public val definition: Definition.Enum,
) : CtxTypedef {
    override fun definition(): Definition.Enum = definition
    override fun symbol(): CtxSymbol = symbol
    override fun children(): List<CtxDefinition> = emptyList()
    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitEnum(this, args)
}

public class CtxStruct(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val fields: List<CtxField>,
    @JvmField public val parent: CtxSymbol?,
    @JvmField public val definition: Definition.Struct,
) : CtxTypedef {
    override fun definition(): Definition.Struct = definition
    override fun symbol(): CtxSymbol = symbol
    override fun children(): List<CtxDefinition> = emptyList()
    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitStruct(this, args)
}

public class CtxUnion(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val variants: List<CtxTypedef>,
    @JvmField public val parent: CtxSymbol?,
    @JvmField public val definition: Definition.Union,
) : CtxTypedef {
    override fun definition(): Definition.Union = definition
    override fun symbol(): CtxSymbol = symbol
    override fun children(): List<CtxTypedef> = variants
    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitUnion(this, args)
}

public class CtxField(
    @JvmField public val name: CtxName,
    @JvmField public val type: CtxType,
)
