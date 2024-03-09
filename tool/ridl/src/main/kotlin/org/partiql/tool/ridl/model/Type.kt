package org.partiql.tool.ridl.model

public class Type(
    @JvmField public val name: Name,
    @JvmField public val type: RType,
) : Definition {

    override fun toString(): String = name.toString()
}
