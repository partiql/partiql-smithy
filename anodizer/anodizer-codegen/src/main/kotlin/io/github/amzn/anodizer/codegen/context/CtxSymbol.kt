package io.github.amzn.anodizer.codegen.context

/**
 * Every definition has a symbol which holds its name and location (path) in the tree.
 *
 * @property tag   Normalized path as string for lookup.
 * @property name  The name of the definition.
 * @property path  The path within the namespace to the definition.
 */
public class CtxSymbol(
    @JvmField public val tag: String,
    @JvmField public val name: CtxName,
    @JvmField public val path: CtxPath,
)
