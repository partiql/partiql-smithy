package io.github.amzn.anodizer.core

/**
 * Name refers to a definition and includes its location relative to the domain root.
 *
 * @property name
 * @property path
 * @property parameters
 */
public class Name(
    @JvmField public val name: String,
    @JvmField public val path: Array<String>,
    @JvmField public val parameters: Array<String> = emptyArray(),
) {

    @JvmField public val tag: String = path.joinToString(".")

    override fun toString(): String = path.joinToString("::")

    override fun equals(other: Any?): Boolean = (other is Name && other.path.contentEquals(path))

    override fun hashCode(): Int = path.contentHashCode()
}
