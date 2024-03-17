package org.partiql.tool.ridl.model

/**
 * References a type by name.
 *
 * @property name   Type identifier.
 * @property base   Type reference to base type if this is an alias.
 */
public class RTypeNamed(
    @JvmField public val name: Name,
    @JvmField public val base: RTypeRef?,
) : RTypeRef {

    override fun toString(): String = name.toString()
}
