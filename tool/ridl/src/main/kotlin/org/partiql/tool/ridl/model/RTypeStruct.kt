package org.partiql.tool.ridl.model

/**
 * Struct type.
 *
 * @property fields
 */
public data class RTypeStruct(
    public val fields: List<Field>,
) : RType {

    public data class Field(
        public val name: String,
        public val type: RType,
    )
}
