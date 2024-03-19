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
        for (item in items) {
            write(writer, item)
        }
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
        for (item in items) {
            write(writer, item)
        }
        writer.stepOut()
    }

    public abstract fun write(writer: IonWriter, item: T)
}

public class Coverage private constructor() {


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


    public class TArrayVar(items: ArrayList<Boolean>) : _ArrayList<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }

        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayVar {
                val items = arrayListOf<Boolean>()
                reader.stepIn()
                while (reader.next() == IonType.BOOL) {
                    items.add(reader.booleanValue())
                }
                reader.stepOut()
                return TArrayVar(items)
            }
        }
    }


    public class TArrayFix(items: Array<Boolean>) : _Array<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }


        companion object {

            @JvmStatic
            fun read(reader: IonReader): TArrayFix {
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
                return TStructPrim(x, y)
            }
        }
    }


    public data class TStruct(
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
            public fun read(reader: IonReader): TStruct {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val x: Int = reader.intValue()
                assert(reader.next() == IonType.INT)
                val y: Long = reader.longValue()
                assert(reader.next() == null)
                reader.stepOut()
                return TStruct(x, y)
            }
        }
    }


}

