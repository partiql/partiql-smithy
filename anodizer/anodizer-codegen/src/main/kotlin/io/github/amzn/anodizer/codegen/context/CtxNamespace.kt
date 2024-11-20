package io.github.amzn.anodizer.codegen.context

/**
 * A contextualized namespace.
 *
 * @property symbol
 * @property definitions
 */
public class CtxNamespace(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val definitions: List<CtxDefinition>,
) : CtxDefinition {

    override fun symbol(): CtxSymbol = symbol

    override fun children(): List<CtxDefinition> = definitions

    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitNamespace(this, args)
}
