package org.partiql.tool.ridl.model

/**
 * References a type by name.
 *
 * @property name   Type identifier.
 */
public data class RTypeNamed(
    public val name: String,
) : RType {

    override fun toString(): String = name
}
