package org.partiql.tool.ridl.model

/**
 * References
 *  - https://docs.oracle.com/cd/E26161_02/html/GettingStartedGuide/avroschemas.html#avro-primitivedatatypes
 *  - https://developers.google.com/protocol-buffers/docs/proto3#scalar
 *  - https://en.wikipedia.org/wiki/JSON#Data_types (scalars only)
 *  - https://amzn.github.io/ion-docs/docs/spec.html (scalars only)
 */
public data class RTypePrimitive(
    public val type: T,
) : RType {

    public enum class T {
        BOOL,
        I32,
        I64,
        F32,
        F64,
        DECIMAL,
        CHAR,
        STRING,
        BYTE,
        BYTES,
    }

    override fun toString(): String = type.name.lowercase()
}
