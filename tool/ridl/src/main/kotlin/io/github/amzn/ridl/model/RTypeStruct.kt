package io.github.amzn.ridl.model

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
        public val type: RTypeRef,
        public val inline: RType? = null,
    )
}
