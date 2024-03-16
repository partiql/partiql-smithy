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


    public data class TBool(@JvmField val value: Boolean) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeBool(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TBool {
                TODO()
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
                TODO()
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
                TODO()
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
                TODO()
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
                TODO()
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
                TODO()
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
                TODO()
            }
        }
    }


    public data class TByteString(@JvmField val value: ByteArray) : IonSerializable {

        public override fun write(writer: IonWriter) {
            writer.writeBlob(value)
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TByteString {
                TODO()
            }
        }
    }


    public class TArrayPrimVar(items: ArrayList<Boolean>) : _ArrayList<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }
    }


    public class TArrayPrimFix(items: Array<Boolean>) : _Array<Boolean>(items) {

        override fun write(writer: IonWriter, item: Boolean) {
            writer.writeBool(item)
        }
    }


    public class TArrayVar(items: ArrayList<TBool>) : _ArrayList<TBool>(items) {

        override fun write(writer: IonWriter, item: TBool) {
            item.write(writer)
        }
    }


    public class TArrayFix(items: Array<TBool>) : _Array<TBool>(items) {

        override fun write(writer: IonWriter, item: TBool) {
            item.write(writer)
        }
    }


    public data class TStructPrim(
        @JvmField val x: Int,
        @JvmField val y: Long,
    ) : IonSerializable {

        public override fun write(writer: IonWriter) {
            // writer.addTypeAnnotation(TAG)
            writer.stepIn(IonType.SEXP)
            writer.writeInt(x.toLong())
            writer.writeInt(y)
            writer.stepOut()
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TStructPrim {
                // val tags = reader.typeAnnotations
                reader.stepIn()
                val x: Int = reader.intValue()
                val y: Long = reader.longValue()
                reader.stepOut()
                return TStructPrim(x, y)
            }
        }
    }


    public data class TStruct(
        @JvmField val x: TI32,
        @JvmField val y: TI64,
    ) : IonSerializable {

        public override fun write(writer: IonWriter) {
            // writer.addTypeAnnotation(TAG)
            writer.stepIn(IonType.SEXP)
            x.write(writer)
            y.write(writer)
            writer.stepOut()
        }

        public companion object {

            @JvmStatic
            public fun read(reader: IonReader): TStruct {
                // val tags = reader.typeAnnotations
                reader.stepIn()
                TODO()
            }
        }
    }


}

