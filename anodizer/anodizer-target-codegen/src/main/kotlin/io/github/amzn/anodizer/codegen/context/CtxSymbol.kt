package io.github.amzn.anodizer.codegen.context

/**
 * Every definition has a symbol which holds its name and location (path) in the tree.
 *
 * @property tag
 * @property name
 * @property path
 */
public class CtxSymbol(
    @JvmField public val tag: String,
    @JvmField public val name: CtxName,
    @JvmField public val path: CtxPath,
)
