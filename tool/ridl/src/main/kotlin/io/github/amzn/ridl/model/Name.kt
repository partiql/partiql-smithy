package io.github.amzn.ridl.model

public class Name(
    @JvmField public val name: String,
    @JvmField public val path: Array<String>,
) {

    override fun toString(): String = path.joinToString("::")

    override fun equals(other: Any?): Boolean = (other is Name && other.path.contentEquals(path))

    override fun hashCode(): Int = path.contentHashCode()
}
