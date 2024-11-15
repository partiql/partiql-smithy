package io.github.amzn.anodizer.codegen.context

import io.github.amzn.anodizer.core.Ion
import io.github.amzn.anodizer.core.Type

/**
 * A contextualized type.
 */
public sealed interface CtxType {

    /**
     * The original type.
     */
    public fun type(): Type
}

public class CtxNamed(
    @JvmField public val symbol: CtxSymbol,
    @JvmField public val type: Type.Named,
) : CtxType {
    override fun type(): Type = type
}

public class CtxArray(
    @JvmField public val item: CtxType,
    @JvmField public val size: Int?,
    @JvmField public val type: Type.Array,
) : CtxType {
    override fun type(): Type = type
}

public class CtxPrimitive(
    @JvmField public val ion: Ion,
    @JvmField public val type: Type.Primitive,
) : CtxType {
    public fun args(): List<Any?> = type.args()
    override fun type(): Type = type
}

public data object CtxUnit : CtxType {
    override fun type(): Type = Type.Unit
}
