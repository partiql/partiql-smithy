package org.partiql.tool.ridl.model

/**
 * Union type
 *
 * Examples,
 *  - union { a, b }
 *  - union { i32, i64 }
 *
 * @property variants
 */
public data class RTypeUnion(
    public val variants: List<Variant>,
) : RType {

    public data class Variant(
        val name: String,
        val type: RType,
    ) {

        override fun toString(): String = "$name $type;"
    }

    override fun toString(): String = "union { ${variants.joinToString() } }"
}
