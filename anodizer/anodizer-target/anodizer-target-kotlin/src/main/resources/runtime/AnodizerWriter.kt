package io.github.amzn.ridl.runtime

import com.amazon.ion.Decimal
import com.amazon.ion.IonType
import com.amazon.ion.IonWriter

public abstract class AnodizerWriter(@JvmField public val writer: IonWriter) {

    public fun flush() {
        writer.flush()
    }

    public fun finish() {
        writer.finish()
    }

    public fun writeDecimal(value: Decimal, precision: Int?, exponent: Int?) {
        if (precision != null && value.precision() > precision) {
            error("Expected decimal with precision <= $precision, got ${value.precision()}")
        }
        if (exponent != null && -value.scale() > exponent) {
            error("Expected decimal with exponent <= $exponent, got -${value.scale()}")
        }
        writer.writeDecimal(value)
    }

    public fun writeBlob(value: ByteArray, size: Int) {
        if (value.size != size) {
            error("ByteArray size `${value.size}` does not match type definition `blob($size)`")
        }
        writer.writeBlob(value)
    }

    public fun writeClob(value: ByteArray, size: Int) {
        if (value.size != size) {
            error("ByteArray size `${value.size}` does not match type definition `clob($size)`")
        }
        writer.writeClob(value)
    }

    public inline fun <T> writeArray(values: Collection<T>, size: Int?, write: (T) -> Unit) {
        if (size != null && values.size != size) {
            error("Collection size `${values.size}` does not match type definition <type>[$size]")
        }
        writer.stepIn(IonType.LIST)
        for (item in values) {
            write(item)
        }
        writer.stepOut()
    }

    public fun writeUnit(): Unit = writer.writeSymbol("unit")
}
