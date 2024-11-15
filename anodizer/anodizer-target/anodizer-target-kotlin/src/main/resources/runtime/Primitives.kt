package io.github.amzn.ridl.runtime

import com.amazon.ion.Decimal

/**
 * Primitive is for named types like, `type a bool`;
 */
public sealed interface IonPrimitive

public open class IonBool(@JvmField public val value: Boolean) : IonPrimitive

public open class IonInt(@JvmField public val value: Long) : IonPrimitive

public open class IonFloat(@JvmField public val value: Double) : IonPrimitive

public open class IonDecimal(
    @JvmField public val value: Decimal,
    @JvmField public val precision: Int? = null,
    @JvmField public val exponent: Int? = null,
) : IonPrimitive

public open class IonString(@JvmField public val value: String) : IonPrimitive

public open class IonBlob(
    @JvmField public val value: ByteArray,
    @JvmField public val size: Int? = null,
) : IonPrimitive {

    init {
        if (size != null && value.size != size) {
            error("Size of `values` does not match given size ($size).")
        }
    }
}

public open class IonClob(
    @JvmField public val value: ByteArray,
    @JvmField public val size: Int? = null,
) : IonPrimitive {

    init {
        if (size != null && value.size != size) {
            error("Size of `values` does not match given size ($size).")
        }
    }
}
