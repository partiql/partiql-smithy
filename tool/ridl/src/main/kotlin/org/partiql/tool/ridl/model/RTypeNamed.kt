package org.partiql.tool.ridl.model

/**
 * References a type by name.
 *
 * @property name   Type name.
 * @property base   Type reference to base type; non-null if this is a type alias.
 */
public data class RTypeNamed(
    @JvmField public val name: Name,
    @JvmField public val base: RTypeRef?,
) : RTypeRef {

    override fun toString(): String = name.toString()
}
