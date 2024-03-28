package io.github.amzn.ridl.model

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
    public val variants: List<Type>,
) : RType {

    override fun toString(): String = "union { ${variants.joinToString() } }"
}
