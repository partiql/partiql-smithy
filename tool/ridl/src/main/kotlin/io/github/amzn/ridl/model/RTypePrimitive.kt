package io.github.amzn.ridl.model

public data class RTypePrimitive(
    @JvmField val kind: Primitive,
) : RTypeRef {

    public val name: String = kind.name.lowercase()

    override fun toString(): String = name
}
