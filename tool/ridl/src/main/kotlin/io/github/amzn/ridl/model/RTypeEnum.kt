package io.github.amzn.ridl.model

/**
 * Enumeration type definition; values are of the form `[A-Z][A-Z_]+`.
 *
 * Examples,
 *  - enum { A, B, C }
 *  - enum { ASC, DESC }
 *
 * @property values
 */
public data class RTypeEnum(
    public val values: List<String>
) : RType {

    override fun toString(): String = "enum { ${values.joinToString()} }"
}
