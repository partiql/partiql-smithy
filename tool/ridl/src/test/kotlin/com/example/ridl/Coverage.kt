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


    public sealed interface TUnion1 : IonSerializable {


        public data class VarA(@JvmField val value: Int) : TUnion1 {

            private val tag: Long = 0

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                writer.writeInt(value.toLong())
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarA {
                    val value: Int = reader.intValue()
                    return VarA(value)
                }
            }
        }


        public data class VarB(@JvmField val value: Long) : TUnion1 {

            private val tag: Long = 1

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                writer.writeInt(value)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarB {
                    val value: Long = reader.longValue()
                    return VarB(value)
                }
            }
        }


        companion object {

            @JvmStatic
            public fun read(reader: IonReader): TUnion1 {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Int = reader.intValue()
                reader.next()
                val variant = when (tag) {
                    0 -> VarA.read(reader)
                    1 -> VarB.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion1`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


    public sealed interface TUnion2 : IonSerializable {


        public data class VarA(@JvmField val value: TStruct) : TUnion2 {

            private val tag: Long = 0

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarA {
                    val value: TStruct = TStruct.read(reader)
                    return VarA(value)
                }
            }
        }


        public data class VarB(@JvmField val value: TStruct) : TUnion2 {

            private val tag: Long = 1

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarB {
                    val value: TStruct = TStruct.read(reader)
                    return VarB(value)
                }
            }
        }


        companion object {

            @JvmStatic
            public fun read(reader: IonReader): TUnion2 {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Int = reader.intValue()
                reader.next()
                val variant = when (tag) {
                    0 -> VarA.read(reader)
                    1 -> VarB.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion2`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


    public sealed interface TUnion3 : IonSerializable {


        public data class VarA(
            @JvmField val x: Int,
            @JvmField val y: Long,
        ) : TUnion3 {


            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(x.toLong())
                writer.writeInt(y)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarA {
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val x: Int = reader.intValue()
                    assert(reader.next() == IonType.INT)
                    val y: Long = reader.longValue()
                    assert(reader.next() == null)
                    reader.stepOut()
                    return VarA(x, y)
                }
            }
        }


        public data class VarB(
            @JvmField val x: Float,
            @JvmField val y: Double,
        ) : TUnion3 {


            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeFloat(x.toDouble())
                writer.writeFloat(y)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarB {
                    reader.stepIn()
                    assert(reader.next() == IonType.FLOAT)
                    val x: Float = reader.doubleValue().toFloat()
                    assert(reader.next() == IonType.FLOAT)
                    val y: Double = reader.doubleValue()
                    assert(reader.next() == null)
                    reader.stepOut()
                    return VarB(x, y)
                }
            }
        }


        companion object {

            @JvmStatic
            public fun read(reader: IonReader): TUnion3 {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Int = reader.intValue()
                reader.next()
                val variant = when (tag) {
                    0 -> VarA.read(reader)
                    1 -> VarB.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion3`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


    public sealed interface TUnion4 : IonSerializable {


        public data class VarA(@JvmField val value: Int) : TUnion4 {

            private val tag: Long = 0

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                writer.writeInt(value.toLong())
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarA {
                    val value: Int = reader.intValue()
                    return VarA(value)
                }
            }
        }


        public data class VarB(@JvmField val value: TStruct) : TUnion4 {

            private val tag: Long = 1

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(tag)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarB {
                    val value: TStruct = TStruct.read(reader)
                    return VarB(value)
                }
            }
        }


        public data class VarC(
            @JvmField val x: Float,
            @JvmField val y: Double,
        ) : TUnion4 {


            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeFloat(x.toDouble())
                writer.writeFloat(y)
                writer.stepOut()
            }

            public companion object {

                @JvmStatic
                public fun read(reader: IonReader): VarC {
                    reader.stepIn()
                    assert(reader.next() == IonType.FLOAT)
                    val x: Float = reader.doubleValue().toFloat()
                    assert(reader.next() == IonType.FLOAT)
                    val y: Double = reader.doubleValue()
                    assert(reader.next() == null)
                    reader.stepOut()
                    return VarC(x, y)
                }
            }
        }


        companion object {

            @JvmStatic
            public fun read(reader: IonReader): TUnion4 {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Int = reader.intValue()
                reader.next()
                val variant = when (tag) {
                    0 -> VarA.read(reader)
                    1 -> VarB.read(reader)
                    2 -> VarC.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion4`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


    public sealed interface TUnion5 : IonSerializable {


        public sealed interface Var1 : TUnion5 {


            public data class VarA(@JvmField val value: Int) : Var1 {

                private val tag: Long = 0

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    writer.writeInt(value.toLong())
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarA {
                        val value: Int = reader.intValue()
                        return VarA(value)
                    }
                }
            }


            public data class VarB(@JvmField val value: Long) : Var1 {

                private val tag: Long = 1

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    writer.writeInt(value)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarB {
                        val value: Long = reader.longValue()
                        return VarB(value)
                    }
                }
            }


            companion object {

                @JvmStatic
                public fun read(reader: IonReader): Var1 {
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val tag: Int = reader.intValue()
                    reader.next()
                    val variant = when (tag) {
                        0 -> VarA.read(reader)
                        1 -> VarB.read(reader)
                        else -> error("Invalid tag `$tag` on union type `Var1`")
                    }
                    reader.stepOut()
                    return variant
                }
            }
        }


        public sealed interface Var2 : TUnion5 {


            public data class VarA(@JvmField val value: TStruct) : Var2 {

                private val tag: Long = 0

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    value.write(writer)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarA {
                        val value: TStruct = TStruct.read(reader)
                        return VarA(value)
                    }
                }
            }


            public data class VarB(@JvmField val value: TStruct) : Var2 {

                private val tag: Long = 1

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    value.write(writer)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarB {
                        val value: TStruct = TStruct.read(reader)
                        return VarB(value)
                    }
                }
            }


            companion object {

                @JvmStatic
                public fun read(reader: IonReader): Var2 {
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val tag: Int = reader.intValue()
                    reader.next()
                    val variant = when (tag) {
                        0 -> VarA.read(reader)
                        1 -> VarB.read(reader)
                        else -> error("Invalid tag `$tag` on union type `Var2`")
                    }
                    reader.stepOut()
                    return variant
                }
            }
        }


        public sealed interface Var3 : TUnion5 {


            public data class VarA(
                @JvmField val x: Int,
                @JvmField val y: Long,
            ) : Var3 {


                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(x.toLong())
                    writer.writeInt(y)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarA {
                        reader.stepIn()
                        assert(reader.next() == IonType.INT)
                        val x: Int = reader.intValue()
                        assert(reader.next() == IonType.INT)
                        val y: Long = reader.longValue()
                        assert(reader.next() == null)
                        reader.stepOut()
                        return VarA(x, y)
                    }
                }
            }


            public data class VarB(
                @JvmField val x: Float,
                @JvmField val y: Double,
            ) : Var3 {


                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeFloat(x.toDouble())
                    writer.writeFloat(y)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarB {
                        reader.stepIn()
                        assert(reader.next() == IonType.FLOAT)
                        val x: Float = reader.doubleValue().toFloat()
                        assert(reader.next() == IonType.FLOAT)
                        val y: Double = reader.doubleValue()
                        assert(reader.next() == null)
                        reader.stepOut()
                        return VarB(x, y)
                    }
                }
            }


            companion object {

                @JvmStatic
                public fun read(reader: IonReader): Var3 {
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val tag: Int = reader.intValue()
                    reader.next()
                    val variant = when (tag) {
                        0 -> VarA.read(reader)
                        1 -> VarB.read(reader)
                        else -> error("Invalid tag `$tag` on union type `Var3`")
                    }
                    reader.stepOut()
                    return variant
                }
            }
        }


        public sealed interface Var4 : TUnion5 {


            public data class VarA(@JvmField val value: Int) : Var4 {

                private val tag: Long = 0

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    writer.writeInt(value.toLong())
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarA {
                        val value: Int = reader.intValue()
                        return VarA(value)
                    }
                }
            }


            public data class VarB(@JvmField val value: TStruct) : Var4 {

                private val tag: Long = 1

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(tag)
                    value.write(writer)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarB {
                        val value: TStruct = TStruct.read(reader)
                        return VarB(value)
                    }
                }
            }


            public data class VarC(
                @JvmField val x: Float,
                @JvmField val y: Double,
            ) : Var4 {


                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeFloat(x.toDouble())
                    writer.writeFloat(y)
                    writer.stepOut()
                }

                public companion object {

                    @JvmStatic
                    public fun read(reader: IonReader): VarC {
                        reader.stepIn()
                        assert(reader.next() == IonType.FLOAT)
                        val x: Float = reader.doubleValue().toFloat()
                        assert(reader.next() == IonType.FLOAT)
                        val y: Double = reader.doubleValue()
                        assert(reader.next() == null)
                        reader.stepOut()
                        return VarC(x, y)
                    }
                }
            }


            companion object {

                @JvmStatic
                public fun read(reader: IonReader): Var4 {
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val tag: Int = reader.intValue()
                    reader.next()
                    val variant = when (tag) {
                        0 -> VarA.read(reader)
                        1 -> VarB.read(reader)
                        2 -> VarC.read(reader)
                        else -> error("Invalid tag `$tag` on union type `Var4`")
                    }
                    reader.stepOut()
                    return variant
                }
            }
        }


        companion object {

            @JvmStatic
            public fun read(reader: IonReader): TUnion5 {
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Int = reader.intValue()
                reader.next()
                val variant = when (tag) {
                    0 -> Var1.read(reader)
                    1 -> Var2.read(reader)
                    2 -> Var3.read(reader)
                    3 -> Var4.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion5`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


}

