package io.github.amzn.anodizer.codegen.context

/**
 * CtxName
 * -----------------------
 * snake -> "my_type"
 * upper -> "MY_TYPE"
 * lower -> "my_type"
 * camel -> "myType"
 * pascal -> "MyType"
 */
public class CtxName(
    @JvmField public val snake: String,
    @JvmField public val camel: String,
    @JvmField public val pascal: String,
    @JvmField public val upper: String,
    @JvmField public val lower: String,
)
