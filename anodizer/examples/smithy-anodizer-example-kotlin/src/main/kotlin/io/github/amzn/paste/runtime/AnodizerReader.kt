package io.github.amzn.paste.runtime

import com.amazon.ion.Decimal
import com.amazon.ion.IonReader
import com.amazon.ion.IonType

public abstract class AnodizerReader(@JvmField public val reader: IonReader) {

    public fun next() {
        reader.next()
    }

    public fun assertType(type: IonType) {
        if (reader.type != type) error("Expected `$type`, got `${reader.type}`")
    }

    public fun assertType(type: IonType, tag: String) {
        if (reader.type != type) error("Expected `$type`, got `${reader.type}`")
        val annotation = reader.typeAnnotations.first()
        if (annotation != tag) error("Expected `$annotation`, got `$tag`")
    }

    public fun assertField(key: String) {
        if (reader.fieldName == key) error("Expected field `$key`, found `$reader.fieldName`")
    }

    public fun assertUnit() {
        val symbol = reader.symbolValue().text
        if (symbol != "unit") {
            error("Expected `unit`, got `$symbol`")
        }
    }

    public fun decimalValue(precision: Int? = null, exponent: Int? = null): Decimal {
        val value = reader.decimalValue()
        if (precision != null && value.precision() > precision) {
            error("Expected decimal with precision <= $precision, got ${value.precision()}")
        }
        if (precision != null && value.precision() != precision) {
            // TODO use _scale_ instead of _exponent_?
            // https://amazon-ion.github.io/ion-schema/docs/cookbook/sql-decimals.html
            error("Expected decimal with precision $precision, got ${value.precision()}")
        }
        return value
    }

    public fun newBytes(size: Int? = null): ByteArray {
        val bytes = reader.newBytes()
        if (size != null && bytes.size != size) {
            error("Expected lob of size $size, got ${bytes.size}")
        }
        return bytes
    }

    public inline fun <T> readArray(read: () -> T): Collection<T> {
        var items = mutableListOf<T>()
        reader.stepIn()
        reader.next()
        while (reader.type != null) {
            val item = read(); reader.next()
            items.add(item)
        }
        reader.stepOut()
        return items
    }

    public inline fun <reified T> readArray(size: Int, read: () -> T): Collection<T> {
        var i = 0
        var items = mutableListOf<T>()
        reader.stepIn()
        reader.next()
        while (reader.type != null) {
            val item = read(); reader.next()
            items.add(item); i += 1
        }
        reader.stepOut()
        if (i != size) {
            error("Ion list size `$i` does not match type definition <type>[$size]")
        }
        return items
    }
}
