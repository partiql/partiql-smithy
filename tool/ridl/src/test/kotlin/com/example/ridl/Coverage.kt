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
}

public abstract class _ArrayList<T>(private val items: ArrayList<T>) : IonSerializable {
    public val size: Int = items.size
    public operator fun get(index: Int): T = items[index]
    public operator fun set(index: Int, value: T): T = items.set(index, value)
    public operator fun iterator(): Iterator<T> = items.iterator()
}

public class Coverage private constructor() {


    public class TArrayPrimVar(private val items: ArrayList<Boolean>) : _ArrayList<Boolean>(items), IonSerializable {

        override fun write(writer: IonWriter) {
            writer.stepIn(IonType.LIST)
            for (item in items) {
                writer.writeBool(item)
            }
            writer.stepOut()
        }

        companion object {


            @JvmStatic
            fun read(reader: IonReader): TArrayPrimVar {
                val items = arrayListOf<Boolean>()
                assert(reader.type == IonType.LIST)
                reader.stepIn()
                while (reader.next() == IonType.BOOL) {
                    items.add(reader.booleanValue())
                }
                reader.stepOut()
                return TArrayPrimVar(items)
            }
        }
    }


    public class TArrayPrimFix(private val items: Array<Boolean>) : _Array<Boolean>(items), IonSerializable {

        override fun write(writer: IonWriter) {
            writer.stepIn(IonType.LIST)
            for (item in items) {
                writer.writeBool(item)
            }
            writer.stepOut()
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


    public class TArrayVar(private val items: ArrayList<Boolean>) : _ArrayList<Boolean>(items), IonSerializable {

        override fun write(writer: IonWriter) {
            writer.stepIn(IonType.LIST)
            for (item in items) {
                writer.writeBool(item)
            }
            writer.stepOut()
        }

        companion object {


            @JvmStatic
            fun read(reader: IonReader): TArrayVar {
                val items = arrayListOf<Boolean>()
                assert(reader.type == IonType.LIST)
                reader.stepIn()
                while (reader.next() == IonType.BOOL) {
                    items.add(reader.booleanValue())
                }
                reader.stepOut()
                return TArrayVar(items)
            }
        }
    }


    public class TArrayFix(private val items: Array<Boolean>) : _Array<Boolean>(items), IonSerializable {

        override fun write(writer: IonWriter) {
            writer.stepIn(IonType.LIST)
            for (item in items) {
                writer.writeBool(item)
            }
            writer.stepOut()
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
                assert(reader.type == IonType.SEXP)
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
                assert(reader.type == IonType.SEXP)
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


    public enum class TEnum : IonSerializable {
        X, Y, Z, ;

        public override fun write(writer: IonWriter) {
            writer.writeSymbol(name)
        }

        public companion object {


            @JvmStatic
            public fun read(reader: IonReader): TEnum = try {
                TEnum.valueOf(reader.symbolValue().text)
            } catch (ex: IllegalArgumentException) {
                // TODO error messaging
                throw ex
            }
        }
    }


    public sealed interface TUnionRefs : IonSerializable {


        public data class Var1(@JvmField val value: Boolean) : TUnionRefs {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                writer.writeBool(value)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 0

                @JvmStatic
                public fun read(reader: IonReader): Var1 {
                    assert(reader.type == IonType.BOOL)
                    val value: Boolean = reader.booleanValue()
                    return Var1(value)
                }
            }
        }


        public data class Var2(@JvmField val value: TArrayPrimVar) : TUnionRefs {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 1

                @JvmStatic
                public fun read(reader: IonReader): Var2 {
                    assert(reader.type == IonType.SEXP)
                    val value: TArrayPrimVar = TArrayPrimVar.read(reader)
                    return Var2(value)
                }
            }
        }


        public data class Var3(@JvmField val value: TEnum) : TUnionRefs {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 2

                @JvmStatic
                public fun read(reader: IonReader): Var3 {
                    assert(reader.type == IonType.SEXP)
                    val value: TEnum = TEnum.read(reader)
                    return Var3(value)
                }
            }
        }


        public data class Var4(@JvmField val value: TStruct) : TUnionRefs {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                value.write(writer)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 3

                @JvmStatic
                public fun read(reader: IonReader): Var4 {
                    assert(reader.type == IonType.SEXP)
                    val value: TStruct = TStruct.read(reader)
                    return Var4(value)
                }
            }
        }


        companion object {


            @JvmStatic
            public fun read(reader: IonReader): TUnionRefs {
                assert(reader.type == IonType.SEXP)
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Long = reader.longValue()
                reader.next()
                val variant = when (tag) {
                    Var1.TAG -> Var1.read(reader)
                    Var2.TAG -> Var2.read(reader)
                    Var3.TAG -> Var3.read(reader)
                    Var4.TAG -> Var4.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnionRefs`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


    public sealed interface TUnion : IonSerializable {


        public data class Var1(@JvmField val value: Boolean) : TUnion {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                writer.writeBool(value)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 0

                @JvmStatic
                public fun read(reader: IonReader): Var1 {
                    assert(reader.type == IonType.BOOL)
                    val value: Boolean = reader.booleanValue()
                    return Var1(value)
                }
            }
        }

        public class Var2(private val items: ArrayList<Boolean>) : _ArrayList<Boolean>(items), TUnion {

            override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                writer.stepIn(IonType.LIST)
                for (item in items) {
                    writer.writeBool(item)
                }
                writer.stepOut()
                writer.stepOut()
            }

            companion object {

                public const val TAG: Long = 1

                @JvmStatic
                fun read(reader: IonReader): Var2 {
                    val items = arrayListOf<Boolean>()
                    assert(reader.type == IonType.LIST)
                    reader.stepIn()
                    while (reader.next() == IonType.BOOL) {
                        items.add(reader.booleanValue())
                    }
                    reader.stepOut()
                    return Var2(items)
                }
            }
        }


        public enum class Var3 : TUnion {
            A, B, C, ;

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                writer.writeSymbol(name)
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 2

                @JvmStatic
                public fun read(reader: IonReader): Var3 = try {
                    Var3.valueOf(reader.symbolValue().text)
                } catch (ex: IllegalArgumentException) {
                    // TODO error messaging
                    throw ex
                }
            }
        }


        public data class Var4(
            @JvmField val x: Boolean,
            @JvmField val y: Boolean,
        ) : TUnion {

            public override fun write(writer: IonWriter) {
                writer.stepIn(IonType.SEXP)
                writer.writeInt(TAG)
                writer.stepIn(IonType.SEXP)
                writer.writeBool(x)
                writer.writeBool(y)
                writer.stepOut()
                writer.stepOut()
            }

            public companion object {

                public const val TAG: Long = 3

                @JvmStatic
                public fun read(reader: IonReader): Var4 {
                    assert(reader.type == IonType.SEXP)
                    reader.stepIn()
                    assert(reader.next() == IonType.BOOL)
                    val x: Boolean = reader.booleanValue()
                    assert(reader.next() == IonType.BOOL)
                    val y: Boolean = reader.booleanValue()
                    assert(reader.next() == null)
                    reader.stepOut()
                    return Var4(x, y)
                }
            }
        }


        public sealed interface Var5 : TUnion {


            public data class VarA(@JvmField val value: Boolean) : Var5 {

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(TAG)
                    writer.writeBool(value)
                    writer.stepOut()
                }

                public companion object {

                    public const val TAG: Long = 0

                    @JvmStatic
                    public fun read(reader: IonReader): VarA {
                        assert(reader.type == IonType.BOOL)
                        val value: Boolean = reader.booleanValue()
                        return VarA(value)
                    }
                }
            }

            public class VarB(private val items: ArrayList<Boolean>) : _ArrayList<Boolean>(items), Var5 {

                override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(TAG)
                    writer.stepIn(IonType.LIST)
                    for (item in items) {
                        writer.writeBool(item)
                    }
                    writer.stepOut()
                    writer.stepOut()
                }

                companion object {

                    public const val TAG: Long = 1

                    @JvmStatic
                    fun read(reader: IonReader): VarB {
                        val items = arrayListOf<Boolean>()
                        assert(reader.type == IonType.LIST)
                        reader.stepIn()
                        while (reader.next() == IonType.BOOL) {
                            items.add(reader.booleanValue())
                        }
                        reader.stepOut()
                        return VarB(items)
                    }
                }
            }


            public enum class VarC : Var5 {
                A, B, C, ;

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(TAG)
                    writer.writeSymbol(name)
                    writer.stepOut()
                }

                public companion object {

                    public const val TAG: Long = 2

                    @JvmStatic
                    public fun read(reader: IonReader): VarC = try {
                        VarC.valueOf(reader.symbolValue().text)
                    } catch (ex: IllegalArgumentException) {
                        // TODO error messaging
                        throw ex
                    }
                }
            }


            public data class VarD(
                @JvmField val x: Boolean,
            ) : Var5 {

                public override fun write(writer: IonWriter) {
                    writer.stepIn(IonType.SEXP)
                    writer.writeInt(TAG)
                    writer.stepIn(IonType.SEXP)
                    writer.writeBool(x)
                    writer.stepOut()
                    writer.stepOut()
                }

                public companion object {

                    public const val TAG: Long = 3

                    @JvmStatic
                    public fun read(reader: IonReader): VarD {
                        assert(reader.type == IonType.SEXP)
                        reader.stepIn()
                        assert(reader.next() == IonType.BOOL)
                        val x: Boolean = reader.booleanValue()
                        assert(reader.next() == null)
                        reader.stepOut()
                        return VarD(x)
                    }
                }
            }


            public sealed interface VarE : Var5 {

                public data class I(@JvmField val value: Int) : VarE {

                    public override fun write(writer: IonWriter) {
                        writer.stepIn(IonType.SEXP)
                        writer.writeInt(TAG)
                        writer.writeInt(value.toLong())
                        writer.stepOut()
                    }

                    public companion object {

                        public const val TAG: Long = 0

                        @JvmStatic
                        public fun read(reader: IonReader): I {
                            assert(reader.type == IonType.INT)
                            val value: Int = reader.intValue()
                            return I(value)
                        }
                    }
                }


                public data class F(@JvmField val value: Double) : VarE {

                    public override fun write(writer: IonWriter) {
                        writer.stepIn(IonType.SEXP)
                        writer.writeInt(TAG)
                        writer.writeFloat(value)
                        writer.stepOut()
                    }

                    public companion object {

                        public const val TAG: Long = 1

                        @JvmStatic
                        public fun read(reader: IonReader): F {
                            assert(reader.type == IonType.FLOAT)
                            val value: Double = reader.doubleValue()
                            return F(value)
                        }
                    }
                }


                companion object {

                    public const val TAG: Long = 4

                    @JvmStatic
                    public fun read(reader: IonReader): VarE {
                        assert(reader.type == IonType.SEXP)
                        reader.stepIn()
                        assert(reader.next() == IonType.INT)
                        val tag: Long = reader.longValue()
                        reader.next()
                        val variant = when (tag) {
                            I.TAG -> I.read(reader)
                            F.TAG -> F.read(reader)
                            else -> error("Invalid tag `$tag` on union type `VarE`")
                        }
                        reader.stepOut()
                        return variant
                    }
                }
            }


            companion object {

                public const val TAG: Long = 4

                @JvmStatic
                public fun read(reader: IonReader): Var5 {
                    assert(reader.type == IonType.SEXP)
                    reader.stepIn()
                    assert(reader.next() == IonType.INT)
                    val tag: Long = reader.longValue()
                    reader.next()
                    val variant = when (tag) {
                        VarA.TAG -> VarA.read(reader)
                        VarB.TAG -> VarB.read(reader)
                        VarC.TAG -> VarC.read(reader)
                        VarD.TAG -> VarD.read(reader)
                        VarE.TAG -> VarE.read(reader)
                        else -> error("Invalid tag `$tag` on union type `Var5`")
                    }
                    reader.stepOut()
                    return variant
                }
            }
        }


        companion object {


            @JvmStatic
            public fun read(reader: IonReader): TUnion {
                assert(reader.type == IonType.SEXP)
                reader.stepIn()
                assert(reader.next() == IonType.INT)
                val tag: Long = reader.longValue()
                reader.next()
                val variant = when (tag) {
                    Var1.TAG -> Var1.read(reader)
                    Var2.TAG -> Var2.read(reader)
                    Var3.TAG -> Var3.read(reader)
                    Var4.TAG -> Var4.read(reader)
                    Var5.TAG -> Var5.read(reader)
                    else -> error("Invalid tag `$tag` on union type `TUnion`")
                }
                reader.stepOut()
                return variant
            }
        }
    }


}

