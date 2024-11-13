package io.github.amzn.anodizer.core

/**
 * Type represents a RIDL type.
 *
 * TODO clean this up one day.
 */
public sealed interface Type {

    /**
     * References a type by name.
     *
     * @property name   The type name.
     */
    public data class Named(
        @JvmField public val name: Name,
    ) : Type {

        override fun toString(): String = name.toString()
    }

    /**
     * Fixed-length or variable-length collection.
     *
     * @property item
     * @property size
     */
    public data class Array(
        @JvmField public val item: Type,
        @JvmField public val size: Int?,
    ) : Type {

        override fun toString(): String = if (size == null) "$item[]" else "$item[$size]"
    }

    /**
     * https://amazon-ion.github.io/ion-docs/docs/spec.html
     *
     * @property ion Ion
     */
    public sealed class Primitive(
        @JvmField public val ion: Ion,
    ) : Type {

        public open fun args(): List<Any?> = emptyList()

        override fun toString(): kotlin.String = ion.name.lowercase()

        public object Void : Primitive(Ion.NULL) {
            override fun toString(): kotlin.String = "void"
        }

        public object Bool : Primitive(Ion.BOOL)

        public object Int : Primitive(Ion.INT)

        public object Float : Primitive(Ion.FLOAT)

        public data class Decimal(
            @JvmField val precision: kotlin.Int?,
            @JvmField val exponent: kotlin.Int?,
        ) : Primitive(Ion.DECIMAL) {

            override fun args(): List<Any?> = listOf(precision, exponent)

            override fun toString(): kotlin.String {
                val p = precision ?: "_"
                val e = exponent ?: "_"
                return "decimal($p,$e)"
            }
        }

        public object String : Primitive(Ion.STRING)

        public data class Blob(@JvmField val size: kotlin.Int?) : Primitive(Ion.BLOB) {

            override fun args(): List<Any?> = listOf(size)

            override fun toString(): kotlin.String = when (size) {
                null -> "blob"
                else -> "blob($size)"
            }
        }

        public data class Clob(@JvmField val size: kotlin.Int?) : Primitive(Ion.CLOB) {

            override fun args(): List<Any?> = listOf(size)

            override fun toString(): kotlin.String = when (size) {
                null -> "clob"
                else -> "clob($size)"
            }
        }
    }

    public object Unit : Type {
        override fun toString(): String = "unit"
    }
}
