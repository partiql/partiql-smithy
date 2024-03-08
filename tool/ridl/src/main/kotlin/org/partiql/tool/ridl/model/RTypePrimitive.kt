package org.partiql.tool.ridl.model

/**
 * References
 *  - https://docs.oracle.com/cd/E26161_02/html/GettingStartedGuide/avroschemas.html#avro-primitivedatatypes
 *  - https://developers.google.com/protocol-buffers/docs/proto3#scalar
 *  - https://en.wikipedia.org/wiki/JSON#Data_types (scalars only)
 *  - https://amzn.github.io/ion-docs/docs/spec.html (scalars only)
 */
public data class RTypePrimitive(@JvmField val kind: Kind) : RType {

    public enum class Kind {
        BOOL,
        INT32,
        INT64,
        FLOAT32,
        FLOAT64,
        STRING,
        BYTE,
        BYTES,
    }

    override fun toString(): String = kind.name.lowercase()
}
