package io.github.amzn.ridl.model

/**
 * Type representing a fixed-length or variable-length collection
 *
 * @property item
 * @property size
 */
public data class RTypeArray(
    public val item: RTypeRef,
    public val size: Int?,
) : RType {

    override fun toString(): String = if (size == null) "$item[]" else "$item[$size]"
}
