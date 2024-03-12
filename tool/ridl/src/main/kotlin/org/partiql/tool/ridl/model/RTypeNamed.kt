package org.partiql.tool.ridl.model

/**
 * References a type by name.
 *
 * @property name   Type identifier.
 */
public class RTypeNamed(
    @JvmField public val name: Name,
) : RTypeRef {

    override fun toString(): String = name.toString()
}
