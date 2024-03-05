package org.partiql.tool.ridl.model

/**
 * Tuple Type
 *
 * Examples,
 *  - (i32, i32)
 *  - (bool, string, string)
 *  - (my_type, f64)
 *
 * @property operands
 */
public data class RTypeTuple(
    public val operands: List<RType>
) : RType {

    override fun toString(): String = "(${operands.joinToString()})"
}
