package org.partiql.tool.ridl.model

/**
 * Type referencing a raw Ion value.
 *
 * @property type
 */
public data class RTypeIon(
    public val type: T,
) : RType {

    public enum class T {
        ANY,
        BOOL,
        INT,
        FLOAT,
        DECIMAL,
        TIMESTAMP,
        STRING,
        SYMBOL,
        BLOB,
        CLOB,
        STRUCT,
        LIST,
        SEXP,
    }

    override fun toString(): String = "ion.${type.name.lowercase()}"
}
