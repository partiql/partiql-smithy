package org.partiql.tool.ridl.model

/**
 * Type representing a fixed-length or variable-length collection
 *
 * @property type
 * @property size
 */
public data class RTypeArray(
    public val type: RType,
    public val size: Int?,
) : RType {

    override fun toString(): String = if (size == null) "$type[]" else "$type[$size]"
}
