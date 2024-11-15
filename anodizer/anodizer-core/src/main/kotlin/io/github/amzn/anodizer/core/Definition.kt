package io.github.amzn.anodizer.core

/**
 * This is a public view of the symbol tree in the RIDL document.
 */
public sealed interface Definition {

    /**
     * Namespace definition holds several definitions.
     */
    public class Namespace(
        @JvmField public val name: Name,
        @JvmField public val definitions: List<Definition>,
    ) : Definition

    /**
     * Sealed interface for type definitions.
     */
    public sealed interface Typedef : Definition

    /**
     * Alias type definition; type is the aliased type.
     */
    public class Alias(
        @JvmField public val name: Name,
        @JvmField public val type: Type,
    ) : Typedef {

        override fun toString(): String = "$name: alias $type"
    }

    /**
     * Enumeration type definition; values are of the form `[A-Z][A-Z_]+`.
     */
    public class Enum(
        @JvmField public val name: Name,
        @JvmField public val values: List<String>,
    ) : Typedef {

        override fun toString(): String = "$name: enum { ${values.joinToString()} }"
    }

    /**
     * Struct type definition.
     */
    public class Struct(
        @JvmField public val name: Name,
        @JvmField public val fields: List<Field>,
    ) : Typedef {

        public class Field(
            @JvmField public val name: String,
            @JvmField public val type: Type,
        )

        override fun toString(): String = buildString {
            append("$name: ")
            append("struct { ")
            append(fields.joinToString { "${it.name}: ${it.type}" })
            append(" }")
        }
    }

    /**
     * Union type definition.
     */
    public class Union(
        @JvmField public val name: Name,
        @JvmField public val variants: List<Typedef>,
    ) : Typedef {

        override fun toString(): String = "$name: union { ${variants.joinToString() } }"
    }
}
