package org.partiql.tool.ridl.model

public data class RTypeMap(
    public val key: RTypePrimitive,
    public val value: RType,
) : RType {

    override fun toString(): String = "map<$key,$value>"
}
