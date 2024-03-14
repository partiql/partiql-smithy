package org.partiql.tool.ridl.codegen.language.kotlin.example

import com.amazon.ion.IonReader
import com.amazon.ion.IonType
import com.amazon.ion.IonWriter

public abstract class _Array<T>(private val items: Array<T>) {
    public val size: Int = items.size
    public operator fun get(index: Int): T = items[index]
    public operator fun set(index: Int, value: T): Unit = items.set(index, value)
    public operator fun iterator(): Iterator<T> = items.iterator()

    public fun write(writer: IonWriter) {
        writer.stepIn(IonType.LIST)
        for (item in items) {
            write(writer, item)
        }
        writer.stepOut()
    }

    public abstract fun write(writer: IonWriter, item: T)
}

public abstract class _ArrayList<T>(private val items: ArrayList<T>) {
    public val size: Int = items.size
    public operator fun get(index: Int): T = items[index]
    public operator fun set(index: Int, value: T): T = items.set(index, value)
    public operator fun iterator(): Iterator<T> = items.iterator()

    public fun write(writer: IonWriter) {
        writer.stepIn(IonType.LIST)
        for (item in items) {
            write(writer, item)
        }
        writer.stepOut()
    }

    public abstract fun write(writer: IonWriter, item: T)
}

public class Example private constructor() {

    public data class Position(
        @JvmField val x: Int,
        @JvmField val y: Int,
        @JvmField val z: Int,
    ) {

        public fun write(writer: IonWriter) {
            writer.addTypeAnnotation(TAG)
            writer.stepIn(IonType.SEXP)
            writer.writeInt(x.toLong())
            writer.writeInt(y.toLong())
            writer.writeInt(z.toLong())
            writer.stepOut()
        }

        public companion object {

            private const val TAG: String = "position"

            @JvmStatic
            public fun read(reader: IonReader): Position {
                val tags = reader.typeAnnotations
                assert(tags.size == 1) { "Expected a single annotation `$TAG`" }
                assert(tags[0] == TAG) { "Expected tag `$TAG`, found `${tags[0]}`" }
                reader.stepIn()
                val x: Int = reader.intValue()
                val y: Int = reader.intValue()
                val z: Int = reader.intValue()
                reader.stepOut()
                return Position(x, y, z)
            }
        }
    }

    public data class Decimal(
        @JvmField val coefficient: Int,
        @JvmField val exponent: Int,
    ) {

        public fun write(writer: IonWriter) {
            writer.addTypeAnnotation(TAG)
            writer.stepIn(IonType.SEXP)
            writer.writeInt(coefficient.toLong())
            writer.writeInt(exponent.toLong())
            writer.stepOut()
        }

        public companion object {

            private const val TAG: String = "decimal"

            @JvmStatic
            public fun read(reader: IonReader): Decimal {
                val tags = reader.typeAnnotations
                assert(tags.size == 1) { "Expected a single annotation `$TAG`" }
                assert(tags[0] == TAG) { "Expected tag `$TAG`, found `${tags[0]}`" }
                reader.stepIn()
                val coefficient: Int = reader.intValue()
                val exponent: Int = reader.intValue()
                reader.stepOut()
                return Decimal(coefficient, exponent)
            }
        }
    }


    public data class Coordinates(
        @JvmField val lat: Decimal,
        @JvmField val lon: Decimal,
    ) {

        public fun write(writer: IonWriter) {
            writer.addTypeAnnotation(TAG)
            writer.stepIn(IonType.SEXP)
            lat.write(writer)
            lon.write(writer)
            writer.stepOut()
        }

        public companion object {

            private const val TAG: String = "coordinates"

            @JvmStatic
            public fun read(reader: IonReader): Coordinates {
                val tags = reader.typeAnnotations
                assert(tags.size == 1) { "Expected a single annotation `$TAG`" }
                assert(tags[0] == TAG) { "Expected tag `$TAG`, found `${tags[0]}`" }
                reader.stepIn()
                val lat: Decimal = TODO()
                val lon: Decimal = TODO()
                reader.stepOut()
                return Coordinates(lat, lon)
            }
        }
    }

    public class ArrayFixed(items: Array<Int>) : _Array<Int>(items) {

        override fun write(writer: IonWriter, item: Int) {
            writer.writeInt(item.toLong())
        }
    }

    public class ArrayVariable(items: ArrayList<Int>) : _ArrayList<Int>(items) {

        override fun write(writer: IonWriter, item: Int) {
            writer.writeInt(item.toLong())
        }
    }


    public enum class Order {
        ASC, DESC, ;

        public fun write(writer: IonWriter) {
            writer.writeSymbol(name)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): Order = try {
                Order.valueOf(reader.symbolValue().text)
            } catch (ex: IllegalArgumentException) {
                // TODO error messaging
                throw ex
            }
        }
    }


    public object Singleton {

        private const val TAG: String = "singleton"

        public fun write(writer: IonWriter) {
            writer.writeSymbol(TAG)
        }
    }

    /**
     * type either union {
     *     v_int int32;
     *     v_float float32;
     * };
     */
    public sealed interface Either {

        public data class VInt(@JvmField val value: Int) : Either {

            public fun write(writer: IonWriter) {
                writer.writeInt(value.toLong())
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VInt {
                    TODO()
                }
            }
        }

        public data class VFloat(@JvmField val value: Int): Either {

            public fun write(writer: IonWriter) {
                writer.writeInt(value.toLong())
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VFloat {
                    TODO()
                }
            }
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): Either {
                val tags = reader.typeAnnotations
                assert(tags.size == 1) { "Union type `Either` is missing a tag" }
                val tag = tags[0].toInt()
                return when (tag) {
                    0 -> VInt.read(reader)
                    1 -> VFloat.read(reader)
                    else -> error("Invalid tag `$tag` on union type `Either`")
                }
            }
        }
    }
}
