package io.github.amzn.anodizer.codegen.context

/**
 * Wraps anodizer model definitions.
 */
public sealed interface CtxDefinition : Ctx {

    /**
     * Ever definition has a symbol (name+location).
     */
    public fun symbol(): CtxSymbol
}
