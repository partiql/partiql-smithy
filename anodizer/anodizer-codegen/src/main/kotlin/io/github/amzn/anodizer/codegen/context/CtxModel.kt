package io.github.amzn.anodizer.codegen.context

/**
 * A contextualized model with a location (symbol root).
 *
 * @property domain
 * @property definitions
 */
public class CtxModel(
    @JvmField public val domain: CtxName,
    @JvmField public val definitions: List<CtxDefinition>,
) : Ctx {

    override fun children(): List<Ctx> = definitions

    override fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R = visitor.visitModel(this, args)
}
