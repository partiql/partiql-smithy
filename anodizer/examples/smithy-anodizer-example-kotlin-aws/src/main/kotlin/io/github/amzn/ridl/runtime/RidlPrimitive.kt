package io.github.amzn.ridl.runtime

import com.amazon.ion.Decimal

public sealed interface RidlPrimitive

public open class RidlBool(@JvmField public val value: Boolean) : RidlPrimitive

public open class RidlInt(@JvmField public val value: Long) : RidlPrimitive

public open class RidlFloat(@JvmField public val value: Double) : RidlPrimitive

public open class RidlDecimal(
    @JvmField public val value: Decimal,
    @JvmField public val precision: Int? = null,
    @JvmField public val exponent: Int? = null,
) : RidlPrimitive

public open class RidlString(@JvmField public val value: String) : RidlPrimitive

public open class RidlBlob(
    @JvmField public val value: ByteArray,
    @JvmField public val size: Int? = null,
) : RidlPrimitive {

    init {
        if (size != null && value.size != size) {
            error("Size of `values` does not match given size ($size).")
        }
    }
}

public open class RidlClob(
    @JvmField public val value: ByteArray,
    @JvmField public val size: Int? = null,
) : RidlPrimitive {

    init {
        if (size != null && value.size != size) {
            error("Size of `values` does not match given size ($size).")
        }
    }
}
