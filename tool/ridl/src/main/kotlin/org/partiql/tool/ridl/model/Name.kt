package org.partiql.tool.ridl.model

public class Name(
    @JvmField public val name: String,
    @JvmField public val path: Array<String>,
) {

    override fun toString(): String = path.joinToString("::")
}
