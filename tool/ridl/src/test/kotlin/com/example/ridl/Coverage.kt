package com.example.ridl

import com.amazon.ion.IonReader
import com.amazon.ion.IonType
import com.amazon.ion.IonWriter

public interface IonSerializable {
    public fun write(writer: IonWriter)
}

public abstract class _Array<T>(private val items: Array<T>) : IonSerializable {
    public val size: Int = items.size
    public operator fun get(index: Int): T = items[index]
    public operator fun set(index: Int, value: T): Unit = items.set(index, value)
    public operator fun iterator(): Iterator<T> = items.iterator()

    public override fun write(writer: IonWriter) {
        writer.stepIn(IonType.LIST)
        for (item in items) { write(writer, item) }
        writer.stepOut()
    }

    public abstract fun write(writer: IonWriter, item: T)
}

public abstract class _ArrayList<T>(private val items: ArrayList<T>) : IonSerializable {
    public val size: Int = items.size
    public operator fun get(index: Int): T = items[index]
    public operator fun set(index: Int, value: T): T = items.set(index, value)
    public operator fun iterator(): Iterator<T> = items.iterator()

    public override fun write(writer: IonWriter) {
        writer.stepIn(IonType.LIST)
        for (item in items) { write(writer, item) }
        writer.stepOut()
    }

    public abstract fun write(writer: IonWriter, item: T)
}

public class Coverage private constructor() {





    public data class TBool(@JvmField val value: Boolean) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeBool(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TBool {
                val value = reader.booleanValue()
                return TBool(value)
            }
        }
    }







    public data class TI32(@JvmField val value: Int) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeInt(value.toLong())
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TI32 {
                val value = reader.intValue()
                return TI32(value)
            }
        }
    }







    public data class TI64(@JvmField val value: Long) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeInt(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TI64 {
                val value = reader.longValue()
                return TI64(value)
            }
        }
    }







    public data class TF32(@JvmField val value: Float) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeFloat(value.toDouble())
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TF32 {
                val value = reader.doubleValue().toFloat()
                return TF32(value)
            }
        }
    }







    public data class TF64(@JvmField val value: Double) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeFloat(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TF64 {
                val value = reader.doubleValue()
                return TF64(value)
            }
        }
    }







    public data class TStr(@JvmField val value: String) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeString(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TStr {
                val value = reader.stringValue()
                return TStr(value)
            }
        }
    }







    public data class TByte(@JvmField val value: Byte) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeBlob(byteArrayOf(value))
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TByte {
                val value = reader.newBytes()[0]
                return TByte(value)
            }
        }
    }







    public data class TBytes(@JvmField val value: ByteArray) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeBlob(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TBytes {
                val value = reader.newBytes()
                return TBytes(value)
            }
        }
    }





    public class TArrayPrimVar(items: ArrayList<Boolean>) : _ArrayList<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }

        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayPrimVar {
                val items = arrayListOf<Boolean>()
                reader.stepIn()
                while (reader.next() == IonType.BOOL) {
                    items.add(reader.booleanValue())
                }
                reader.stepOut()
                return TArrayPrimVar(items)
            }
        }
    }







    public class TArrayPrimFix(items: Array<Boolean>) : _Array<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }


        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayPrimFix {
                var i = 0
                val n = 10
                val items = arrayOfNulls<Boolean>(n)
                reader.stepIn()
                while (reader.next() == IonType.BOOL) {
                    if (i == n) {
                        error("Expected array of len `$n`, found len $i")
                    }
                    items[i++] = reader.booleanValue()
                }
                if (i != n) {
                    error("Expected array of len `$n`, found len $i")
                }
                reader.stepOut()
                return TArrayPrimFix(items.requireNoNulls())
            }
        }
    }







    public class TArrayVar(items: ArrayList<TBool>) : _ArrayList<TBool>(items) {

        override fun write(writer: IonWriter, item: TBool) {
            item.write(writer)
        }

        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayVar {
                val items = arrayListOf<TBool>()
                reader.stepIn()
                while (reader.next() == IonType.SEXP) {
                    items.add(TBool.read(reader))
                }
                reader.stepOut()
                return TArrayVar(items)
            }
        }
    }







    public class TArrayFix(items: Array<TBool>) : _Array<TBool>(items) {

        override fun write(writer: IonWriter, item: TBool) {
            item.write(writer)
        }


        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayFix {
                var i = 0
                val n = 10
                val items = arrayOfNulls<TBool>(n)
                reader.stepIn()
                while (reader.next() == IonType.SEXP) {
                    if (i == n) {
                        error("Expected array of len `$n`, found len $i")
                    }
                    items[i++] = TBool.read(reader)
                }
                if (i != n) {
                    error("Expected array of len `$n`, found len $i")
                }
                reader.stepOut()
                return TArrayFix(items.requireNoNulls())
            }
        }
    }










    public data class TStructPrim(
        @JvmField val x: Int,
        @JvmField val y: Long,
    ) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.stepIn(IonType.SEXP)
            writer.writeInt(x.toLong())
            writer.writeInt(y)
            writer.stepOut()
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TStructPrim {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val x: Int = reader.intValue()
                assert(reader.next() == IonType.INT)
                val y: Long = reader.longValue()
                assert(reader.next() == null)
                reader.stepOut()
                return TStructPrim(x,y,)
            }
        }
    }







    public data class TStruct(
        @JvmField val x: TI32,
        @JvmField val y: TI64,
    ) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.stepIn(IonType.SEXP)
            x.write(writer)
            y.write(writer)
            writer.stepOut()
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TStruct {
                reader.stepIn()
                assert(reader.next() == IonType.SEXP)
                val x: TI32 = TI32.read(reader)
                assert(reader.next() == IonType.SEXP)
                val y: TI64 = TI64.read(reader)
                assert(reader.next() == null)
                reader.stepOut()
                return TStruct(x,y,)
            }
        }
    }



    public class Test private constructor() {



        public class Nested(items: ArrayList<Int>) : _ArrayList<Int>(items) {

            override fun write(writer: IonWriter, item: Int) {
                writer.writeInt(item.toLong())
            }

            companion object {

                @JvmStatic
                fun read(reader: IonReader): Nested {
                    val items = arrayListOf<Int>()
                    reader.stepIn()
                    while (reader.next() == IonType.INT) {
                        items.add(reader.intValue())
                    }
                    reader.stepOut()
                    return Nested(items)
                }
            }
        }





    }

}

