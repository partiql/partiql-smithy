package org.partiql.tool.ridl.model

/**
 * Typed list of variable length.
 *
 * Examples,
 *  - list<i32>
 *  - list<bool>
 *  - list<my_named_type>
 *
 * @property items
 */
public data class RTypeList(
    public val items: RType,
) : RType {

    override fun toString(): String = "list<$items>"
}
