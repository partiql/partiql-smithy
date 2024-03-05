package org.partiql.tool.ridl.model

/**
 * Type representing a fixed-size collection of primitives.
 *
 * Examples
 *  - char[2]
 *  - byte[16]
 *
 * @property type
 * @property size
 */
public data class RTypeArray(
    public val type: RTypePrimitive,
    public val size: Int,
) : RType {

    override fun toString(): String = "$type[$size]"
}
