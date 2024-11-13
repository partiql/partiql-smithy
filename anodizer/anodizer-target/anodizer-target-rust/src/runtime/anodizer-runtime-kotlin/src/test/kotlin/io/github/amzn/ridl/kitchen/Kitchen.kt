package io.github.amzn.ridl.kitchen

import com.amazon.ion.*
import com.amazon.ion.IonWriter
import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import io.github.amzn.ridl.runtime.*
import java.io.InputStream
import java.io.OutputStream

public class Kitchen private constructor() {

    public companion object {

        @JvmStatic
        private fun toString(write: (KitchenWriter) -> Unit): String {
            val sb = StringBuilder()
            val out = IonTextWriterBuilder.pretty().build(sb)
            val writer = KitchenWriter.text(out)
            write(writer)
            return sb.toString()
        }
    }

    public class BoxBool(value: Boolean) : RidlBool(value) {

        override fun toString(): String = toString { it.writeBoxBool(this) }
    }

    public class BoxInt(value: Long) : RidlInt(value) {

        override fun toString(): String = toString { it.writeBoxInt(this) }
    }

    public class BoxFloat(value: Double) : RidlFloat(value) {

        override fun toString(): String = toString { it.writeBoxFloat(this) }
    }

    public class BoxDecimal(value: Decimal) : RidlDecimal(value) {

        override fun toString(): String = toString { it.writeBoxDecimal(this) }
    }

    public class BoxStr(value: String) : RidlString(value) {

        override fun toString(): String = toString { it.writeBoxStr(this) }
    }

    public class BoxBlob(value: ByteArray) : RidlBlob(value) {

        override fun toString(): String = toString { it.writeBoxBlob(this) }
    }

    public class BoxClob(value: ByteArray) : RidlClob(value) {

        override fun toString(): String = toString { it.writeBoxClob(this) }
    }

    public class DecimalA(value: Decimal) : RidlDecimal(value, 1, 2) {

        override fun toString(): String = toString { it.writeDecimalA(this) }
    }

    public class DecimalB(value: Decimal) : RidlDecimal(value, 1) {

        override fun toString(): String = toString { it.writeDecimalB(this) }
    }

    public class DecimalC(value: Decimal) : RidlDecimal(value, 2) {

        override fun toString(): String = toString { it.writeDecimalC(this) }
    }

    public class BlobA(value: ByteArray) : RidlBlob(value, 1) {

        override fun toString(): String = toString { it.writeBlobA(this) }
    }

    public class ClobB(value: ByteArray) : RidlClob(value, 1) {

        override fun toString(): String = toString { it.writeClobB(this) }
    }

    public class ArrA(@JvmField public val values: Collection<Boolean>) : Iterable<Boolean> {

        override fun iterator(): Iterator<Boolean> = values.iterator()

        override fun toString(): String = toString { it.writeArrA(this) }
    }

    public class ArrB(@JvmField public val values: Collection<Boolean>) : Iterable<Boolean> {

        override fun iterator(): Iterator<Boolean> = values.iterator()

        override fun toString(): String = toString { it.writeArrB(this) }
    }

    public class ArrC(@JvmField public val values: Collection<Kitchen.BoxBool>) : Iterable<Kitchen.BoxBool> {

        override fun iterator(): Iterator<Kitchen.BoxBool> = values.iterator()

        override fun toString(): String = toString { it.writeArrC(this) }
    }

    public class ArrD(@JvmField public val values: Collection<Kitchen.BoxBool>) : Iterable<Kitchen.BoxBool> {

        override fun iterator(): Iterator<Kitchen.BoxBool> = values.iterator()

        override fun toString(): String = toString { it.writeArrD(this) }
    }

    public class ArrNested(@JvmField public val values: Collection<Collection<Boolean>>) :
        Iterable<Collection<Boolean>> {

        override fun iterator(): Iterator<Collection<Boolean>> = values.iterator()

        override fun toString(): String = toString { it.writeArrNested(this) }
    }

    public enum class EnumA {
        HELLO_WORLD, GOODNIGHT_MOON,
        ;

        override fun toString(): String = toString { it.writeEnumA(this) }
    }

    public object Nil {

        override fun toString(): String = toString { it.writeNil(this) }
    }

    /**
     * Definition:
     *
     *      struct_a: struct { x: bool, y: int, z: string }
     *
     * Ion Text Encoding:
     *
     *      'tag'::{
     *         ...
     *       }
     *
     * Ion Packed Encoding:
     *
     *      (...)
     */
    public data class StructA(
        @JvmField var x: Boolean,
        @JvmField var y: Long,
        @JvmField var z: String,
    ) {

        override fun toString(): String = toString { it.writeStructA(this) }
    }

    /**
     * Definition:
     *
     *      struct_a: struct { x: bool, y: int, z: string }
     *
     * Ion Text Encoding:
     *
     *      'tag'::{
     *         ...
     *       }
     *
     * Ion Packed Encoding:
     *
     *      (...)
     */
    public data class StructAlias(
        @JvmField var x: Boolean,
        @JvmField var y: Long,
        @JvmField var z: String,
    ) {

        override fun toString(): String = toString { it.writeStructAlias(this) }
    }

    /**
     * Definition:
     *
     *      struct_b: struct { x: bool[], y: int[], z: string[] }
     *
     * Ion Text Encoding:
     *
     *      'tag'::{
     *         ...
     *       }
     *
     * Ion Packed Encoding:
     *
     *      (...)
     */
    public data class StructB(
        @JvmField var x: Collection<Boolean>,
        @JvmField var y: Collection<Long>,
        @JvmField var z: Collection<String>,
    ) {

        override fun toString(): String = toString { it.writeStructB(this) }
    }

    /**
     * Definition:
     *
     *      struct_c: struct { x: decimal(1,2), y: blob(10), z: clob(10) }
     *
     * Ion Text Encoding:
     *
     *      'tag'::{
     *         ...
     *       }
     *
     * Ion Packed Encoding:
     *
     *      (...)
     */
    public data class StructC(
        @JvmField var x: Decimal,
        @JvmField var y: ByteArray,
        @JvmField var z: ByteArray,
    ) {

        override fun toString(): String = toString { it.writeStructC(this) }
    }

    public sealed interface UnionA {

        public class A(value: Boolean) : RidlBool(value), Kitchen.UnionA {

            override fun toString(): String = toString { it.writeUnionAA(this) }
        }

        public class B(value: Long) : RidlInt(value), Kitchen.UnionA {

            override fun toString(): String = toString { it.writeUnionAB(this) }
        }

        public class C(value: String) : RidlString(value), Kitchen.UnionA {

            override fun toString(): String = toString { it.writeUnionAC(this) }
        }
    }

    public sealed interface UnionB {

        /**
         * Definition:
         *
         *      union_b::a: struct { x: bool, y: int, z: string }
         *
         * Ion Text Encoding:
         *
         *      'tag'::{
         *         ...
         *       }
         *
         * Ion Packed Encoding:
         *
         *      (...)
         */
        public data class A(
            @JvmField var x: Boolean,
            @JvmField var y: Long,
            @JvmField var z: String,
        ) : Kitchen.UnionB {

            override fun toString(): String = toString { it.writeUnionBA(this) }
        }

        /**
         * Definition:
         *
         *      union_b::b: struct { x: bool[], y: int[], z: string[] }
         *
         * Ion Text Encoding:
         *
         *      'tag'::{
         *         ...
         *       }
         *
         * Ion Packed Encoding:
         *
         *      (...)
         */
        public data class B(
            @JvmField var x: Collection<Boolean>,
            @JvmField var y: Collection<Long>,
            @JvmField var z: Collection<String>,
        ) : Kitchen.UnionB {

            override fun toString(): String = toString { it.writeUnionBB(this) }
        }

        /**
         * Definition:
         *
         *      union_b::c: struct { x: decimal(1,2), y: blob(10), z: clob(10) }
         *
         * Ion Text Encoding:
         *
         *      'tag'::{
         *         ...
         *       }
         *
         * Ion Packed Encoding:
         *
         *      (...)
         */
        public data class C(
            @JvmField var x: Decimal,
            @JvmField var y: ByteArray,
            @JvmField var z: ByteArray,
        ) : Kitchen.UnionB {

            override fun toString(): String = toString { it.writeUnionBC(this) }
        }
    }

    public sealed interface UnionC {

        public sealed interface A : Kitchen.UnionC {

            public class A(value: Boolean) : RidlBool(value), Kitchen.UnionC.A {

                override fun toString(): String = toString { it.writeUnionCAA(this) }
            }

            public class B(value: Long) : RidlInt(value), Kitchen.UnionC.A {

                override fun toString(): String = toString { it.writeUnionCAB(this) }
            }

            public class C(value: String) : RidlString(value), Kitchen.UnionC.A {

                override fun toString(): String = toString { it.writeUnionCAC(this) }
            }
        }

        public sealed interface B : Kitchen.UnionC {

            public class A(@JvmField public val values: Collection<Boolean>) : Iterable<Boolean>, Kitchen.UnionC.B {

                override fun iterator(): Iterator<Boolean> = values.iterator()

                override fun toString(): String = toString { it.writeUnionCBA(this) }
            }

            public class B(@JvmField public val values: Collection<Long>) : Iterable<Long>, Kitchen.UnionC.B {

                override fun iterator(): Iterator<Long> = values.iterator()

                override fun toString(): String = toString { it.writeUnionCBB(this) }
            }

            public class C(@JvmField public val values: Collection<String>) : Iterable<String>, Kitchen.UnionC.B {

                override fun iterator(): Iterator<String> = values.iterator()

                override fun toString(): String = toString { it.writeUnionCBC(this) }
            }
        }
    }
}

public abstract class KitchenWriter(writer: IonWriter) : RidlWriter(writer) {

    public abstract fun writeBoxBool(value: Kitchen.BoxBool)

    public abstract fun writeBoxInt(value: Kitchen.BoxInt)

    public abstract fun writeBoxFloat(value: Kitchen.BoxFloat)

    public abstract fun writeBoxDecimal(value: Kitchen.BoxDecimal)

    public abstract fun writeBoxStr(value: Kitchen.BoxStr)

    public abstract fun writeBoxBlob(value: Kitchen.BoxBlob)

    public abstract fun writeBoxClob(value: Kitchen.BoxClob)

    public abstract fun writeDecimalA(value: Kitchen.DecimalA)

    public abstract fun writeDecimalB(value: Kitchen.DecimalB)

    public abstract fun writeDecimalC(value: Kitchen.DecimalC)

    public abstract fun writeBlobA(value: Kitchen.BlobA)

    public abstract fun writeClobB(value: Kitchen.ClobB)

    public abstract fun writeArrA(value: Kitchen.ArrA)

    public abstract fun writeArrB(value: Kitchen.ArrB)

    public abstract fun writeArrC(value: Kitchen.ArrC)

    public abstract fun writeArrD(value: Kitchen.ArrD)

    public abstract fun writeArrNested(value: Kitchen.ArrNested)

    public abstract fun writeEnumA(value: Kitchen.EnumA)

    public abstract fun writeNil(value: Kitchen.Nil)

    public abstract fun writeStructA(value: Kitchen.StructA)

    public abstract fun writeStructAlias(value: Kitchen.StructAlias)

    public abstract fun writeStructB(value: Kitchen.StructB)

    public abstract fun writeStructC(value: Kitchen.StructC)

    public abstract fun writeUnionA(value: Kitchen.UnionA)

    public abstract fun writeUnionAA(value: Kitchen.UnionA.A)

    public abstract fun writeUnionAB(value: Kitchen.UnionA.B)

    public abstract fun writeUnionAC(value: Kitchen.UnionA.C)

    public abstract fun writeUnionB(value: Kitchen.UnionB)

    public abstract fun writeUnionBA(value: Kitchen.UnionB.A)

    public abstract fun writeUnionBB(value: Kitchen.UnionB.B)

    public abstract fun writeUnionBC(value: Kitchen.UnionB.C)

    public abstract fun writeUnionC(value: Kitchen.UnionC)

    public abstract fun writeUnionCA(value: Kitchen.UnionC.A)

    public abstract fun writeUnionCAA(value: Kitchen.UnionC.A.A)

    public abstract fun writeUnionCAB(value: Kitchen.UnionC.A.B)

    public abstract fun writeUnionCAC(value: Kitchen.UnionC.A.C)

    public abstract fun writeUnionCB(value: Kitchen.UnionC.B)

    public abstract fun writeUnionCBA(value: Kitchen.UnionC.B.A)

    public abstract fun writeUnionCBB(value: Kitchen.UnionC.B.B)

    public abstract fun writeUnionCBC(value: Kitchen.UnionC.B.C)

    public companion object {

        @JvmStatic
        public fun text(output: Appendable): KitchenWriter = Text(IonTextWriterBuilder.standard().build(output))

        @JvmStatic
        public fun text(writer: IonWriter): KitchenWriter = Text(writer)

        @JvmStatic
        public fun packed(output: OutputStream): KitchenWriter = Packed(IonBinaryWriterBuilder.standard().build(output))

        @JvmStatic
        public fun packed(writer: IonWriter): KitchenWriter = Packed(writer)
    }

    private class Text(writer: IonWriter) : KitchenWriter(writer) {

        override fun writeBoxBool(value: Kitchen.BoxBool) {
            writer.setTypeAnnotations("box_bool")
            writer.writeBool(value.value)
        }

        override fun writeBoxInt(value: Kitchen.BoxInt) {
            writer.setTypeAnnotations("box_int")
            writer.writeInt(value.value)
        }

        override fun writeBoxFloat(value: Kitchen.BoxFloat) {
            writer.setTypeAnnotations("box_float")
            writer.writeFloat(value.value)
        }

        override fun writeBoxDecimal(value: Kitchen.BoxDecimal) {
            writer.setTypeAnnotations("box_decimal")
            writer.writeDecimal(value.value)
        }

        override fun writeBoxStr(value: Kitchen.BoxStr) {
            writer.setTypeAnnotations("box_str")
            writer.writeString(value.value)
        }

        override fun writeBoxBlob(value: Kitchen.BoxBlob) {
            writer.setTypeAnnotations("box_blob")
            writer.writeBlob(value.value)
        }

        override fun writeBoxClob(value: Kitchen.BoxClob) {
            writer.setTypeAnnotations("box_clob")
            writer.writeClob(value.value)
        }

        override fun writeDecimalA(value: Kitchen.DecimalA) {
            writer.setTypeAnnotations("decimal_a")
            writeDecimal(value.value, 1, 2)
        }

        override fun writeDecimalB(value: Kitchen.DecimalB) {
            writer.setTypeAnnotations("decimal_b")
            writeDecimal(value.value, 1, null)
        }

        override fun writeDecimalC(value: Kitchen.DecimalC) {
            writer.setTypeAnnotations("decimal_c")
            writeDecimal(value.value, null, 2)
        }

        override fun writeBlobA(value: Kitchen.BlobA) {
            writer.setTypeAnnotations("blob_a")
            writeBlob(value.value, 1)
        }

        override fun writeClobB(value: Kitchen.ClobB) {
            writer.setTypeAnnotations("clob_b")
            writeClob(value.value, 1)
        }

        override fun writeArrA(value: Kitchen.ArrA) {
            writer.setTypeAnnotations("arr_a")
            writeArray(value.values, null) { writer.writeBool(it) }
        }

        override fun writeArrB(value: Kitchen.ArrB) {
            writer.setTypeAnnotations("arr_b")
            writeArray(value.values, 10) { writer.writeBool(it) }
        }

        override fun writeArrC(value: Kitchen.ArrC) {
            writer.setTypeAnnotations("arr_c")
            writeArray(value.values, null) { writeBoxBool(it) }
        }

        override fun writeArrD(value: Kitchen.ArrD) {
            writer.setTypeAnnotations("arr_d")
            writeArray(value.values, 10) { writeBoxBool(it) }
        }

        override fun writeArrNested(value: Kitchen.ArrNested) {
            writer.setTypeAnnotations("arr_nested")
            writeArray(value.values, null) { writeArray(it, null) { writer.writeBool(it) } }
        }

        override fun writeEnumA(value: Kitchen.EnumA) {
            writer.setTypeAnnotations("enum_a")
            writer.writeSymbol(value.name)
        }

        override fun writeNil(value: Kitchen.Nil) {
            writer.setTypeAnnotations("nil")
            writer.writeSymbol("unit")
        }

        override fun writeStructA(value: Kitchen.StructA) {
            writer.setTypeAnnotations("struct_a")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writer.writeBool(value.x)
            writer.setFieldName("y"); writer.writeInt(value.y)
            writer.setFieldName("z"); writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeStructAlias(value: Kitchen.StructAlias) {
            writer.setTypeAnnotations("struct_alias")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writer.writeBool(value.x)
            writer.setFieldName("y"); writer.writeInt(value.y)
            writer.setFieldName("z"); writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeStructB(value: Kitchen.StructB) {
            writer.setTypeAnnotations("struct_b")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writeArray(value.x, null) { writer.writeBool(it) }
            writer.setFieldName("y"); writeArray(value.y, null) { writer.writeInt(it) }
            writer.setFieldName("z"); writeArray(value.z, null) { writer.writeString(it) }
            writer.stepOut()
        }

        override fun writeStructC(value: Kitchen.StructC) {
            writer.setTypeAnnotations("struct_c")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writeDecimal(value.x, 1, 2)
            writer.setFieldName("y"); writeBlob(value.y, 10)
            writer.setFieldName("z"); writeClob(value.z, 10)
            writer.stepOut()
        }

        override fun writeUnionA(value: Kitchen.UnionA) {
            when (value) {
                is Kitchen.UnionA.A -> writeUnionAA(value)
                is Kitchen.UnionA.B -> writeUnionAB(value)
                is Kitchen.UnionA.C -> writeUnionAC(value)
            }
        }

        override fun writeUnionAA(value: Kitchen.UnionA.A) {
            writer.setTypeAnnotations("union_a.a")
            writer.writeBool(value.value)
        }

        override fun writeUnionAB(value: Kitchen.UnionA.B) {
            writer.setTypeAnnotations("union_a.b")
            writer.writeInt(value.value)
        }

        override fun writeUnionAC(value: Kitchen.UnionA.C) {
            writer.setTypeAnnotations("union_a.c")
            writer.writeString(value.value)
        }

        override fun writeUnionB(value: Kitchen.UnionB) {
            when (value) {
                is Kitchen.UnionB.A -> writeUnionBA(value)
                is Kitchen.UnionB.B -> writeUnionBB(value)
                is Kitchen.UnionB.C -> writeUnionBC(value)
            }
        }

        override fun writeUnionBA(value: Kitchen.UnionB.A) {
            writer.setTypeAnnotations("union_b.a")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writer.writeBool(value.x)
            writer.setFieldName("y"); writer.writeInt(value.y)
            writer.setFieldName("z"); writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeUnionBB(value: Kitchen.UnionB.B) {
            writer.setTypeAnnotations("union_b.b")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writeArray(value.x, null) { writer.writeBool(it) }
            writer.setFieldName("y"); writeArray(value.y, null) { writer.writeInt(it) }
            writer.setFieldName("z"); writeArray(value.z, null) { writer.writeString(it) }
            writer.stepOut()
        }

        override fun writeUnionBC(value: Kitchen.UnionB.C) {
            writer.setTypeAnnotations("union_b.c")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("x"); writeDecimal(value.x, 1, 2)
            writer.setFieldName("y"); writeBlob(value.y, 10)
            writer.setFieldName("z"); writeClob(value.z, 10)
            writer.stepOut()
        }

        override fun writeUnionC(value: Kitchen.UnionC) {
            when (value) {
                is Kitchen.UnionC.A -> writeUnionCA(value)
                is Kitchen.UnionC.B -> writeUnionCB(value)
            }
        }

        override fun writeUnionCA(value: Kitchen.UnionC.A) {
            when (value) {
                is Kitchen.UnionC.A.A -> writeUnionCAA(value)
                is Kitchen.UnionC.A.B -> writeUnionCAB(value)
                is Kitchen.UnionC.A.C -> writeUnionCAC(value)
            }
        }

        override fun writeUnionCAA(value: Kitchen.UnionC.A.A) {
            writer.setTypeAnnotations("union_c.a.a")
            writer.writeBool(value.value)
        }

        override fun writeUnionCAB(value: Kitchen.UnionC.A.B) {
            writer.setTypeAnnotations("union_c.a.b")
            writer.writeInt(value.value)
        }

        override fun writeUnionCAC(value: Kitchen.UnionC.A.C) {
            writer.setTypeAnnotations("union_c.a.c")
            writer.writeString(value.value)
        }

        override fun writeUnionCB(value: Kitchen.UnionC.B) {
            when (value) {
                is Kitchen.UnionC.B.A -> writeUnionCBA(value)
                is Kitchen.UnionC.B.B -> writeUnionCBB(value)
                is Kitchen.UnionC.B.C -> writeUnionCBC(value)
            }
        }

        override fun writeUnionCBA(value: Kitchen.UnionC.B.A) {
            writer.setTypeAnnotations("union_c.b.a")
            writeArray(value.values, null) { writer.writeBool(it) }
        }

        override fun writeUnionCBB(value: Kitchen.UnionC.B.B) {
            writer.setTypeAnnotations("union_c.b.b")
            writeArray(value.values, null) { writer.writeInt(it) }
        }

        override fun writeUnionCBC(value: Kitchen.UnionC.B.C) {
            writer.setTypeAnnotations("union_c.b.c")
            writeArray(value.values, null) { writer.writeString(it) }
        }
    }

    private class Packed(writer: IonWriter) : KitchenWriter(writer) {

        override fun writeBoxBool(value: Kitchen.BoxBool) {
            writer.writeBool(value.value)
        }

        override fun writeBoxInt(value: Kitchen.BoxInt) {
            writer.writeInt(value.value)
        }

        override fun writeBoxFloat(value: Kitchen.BoxFloat) {
            writer.writeFloat(value.value)
        }

        override fun writeBoxDecimal(value: Kitchen.BoxDecimal) {
            writer.writeDecimal(value.value)
        }

        override fun writeBoxStr(value: Kitchen.BoxStr) {
            writer.writeString(value.value)
        }

        override fun writeBoxBlob(value: Kitchen.BoxBlob) {
            writer.writeBlob(value.value)
        }

        override fun writeBoxClob(value: Kitchen.BoxClob) {
            writer.writeClob(value.value)
        }

        override fun writeDecimalA(value: Kitchen.DecimalA) {
            writeDecimal(value.value, 1, 2)
        }

        override fun writeDecimalB(value: Kitchen.DecimalB) {
            writeDecimal(value.value, 1, null)
        }

        override fun writeDecimalC(value: Kitchen.DecimalC) {
            writeDecimal(value.value, null, 2)
        }

        override fun writeBlobA(value: Kitchen.BlobA) {
            writeBlob(value.value, 1)
        }

        override fun writeClobB(value: Kitchen.ClobB) {
            writeClob(value.value, 1)
        }

        override fun writeArrA(value: Kitchen.ArrA) {
            writeArray(value.values, null) { writer.writeBool(it) }
        }

        override fun writeArrB(value: Kitchen.ArrB) {
            writeArray(value.values, 10) { writer.writeBool(it) }
        }

        override fun writeArrC(value: Kitchen.ArrC) {
            writeArray(value.values, null) { writeBoxBool(it) }
        }

        override fun writeArrD(value: Kitchen.ArrD) {
            writeArray(value.values, 10) { writeBoxBool(it) }
        }

        override fun writeArrNested(value: Kitchen.ArrNested) {
            writeArray(value.values, null) { writeArray(it, null) { writer.writeBool(it) } }
        }

        override fun writeEnumA(value: Kitchen.EnumA) {
            writer.writeSymbol(value.name)
        }

        override fun writeNil(value: Kitchen.Nil) {
            writer.writeSymbol("unit")
        }

        override fun writeStructA(value: Kitchen.StructA) {
            writer.stepIn(IonType.SEXP)
            writer.writeBool(value.x)
            writer.writeInt(value.y)
            writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeStructAlias(value: Kitchen.StructAlias) {
            writer.stepIn(IonType.SEXP)
            writer.writeBool(value.x)
            writer.writeInt(value.y)
            writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeStructB(value: Kitchen.StructB) {
            writer.stepIn(IonType.SEXP)
            writeArray(value.x, null) { writer.writeBool(it) }
            writeArray(value.y, null) { writer.writeInt(it) }
            writeArray(value.z, null) { writer.writeString(it) }
            writer.stepOut()
        }

        override fun writeStructC(value: Kitchen.StructC) {
            writer.stepIn(IonType.SEXP)
            writeDecimal(value.x, 1, 2)
            writeBlob(value.y, 10)
            writeClob(value.z, 10)
            writer.stepOut()
        }

        override fun writeUnionA(value: Kitchen.UnionA) {
            writer.stepIn(IonType.SEXP)
            when (value) {
                is Kitchen.UnionA.A -> {
                    writer.writeInt(0L)
                    writeUnionAA(value)
                }
                is Kitchen.UnionA.B -> {
                    writer.writeInt(1L)
                    writeUnionAB(value)
                }
                is Kitchen.UnionA.C -> {
                    writer.writeInt(2L)
                    writeUnionAC(value)
                }
            }
            writer.stepOut()
        }

        override fun writeUnionAA(value: Kitchen.UnionA.A) {
            writer.writeBool(value.value)
        }

        override fun writeUnionAB(value: Kitchen.UnionA.B) {
            writer.writeInt(value.value)
        }

        override fun writeUnionAC(value: Kitchen.UnionA.C) {
            writer.writeString(value.value)
        }

        override fun writeUnionB(value: Kitchen.UnionB) {
            writer.stepIn(IonType.SEXP)
            when (value) {
                is Kitchen.UnionB.A -> {
                    writer.writeInt(0L)
                    writeUnionBA(value)
                }
                is Kitchen.UnionB.B -> {
                    writer.writeInt(1L)
                    writeUnionBB(value)
                }
                is Kitchen.UnionB.C -> {
                    writer.writeInt(2L)
                    writeUnionBC(value)
                }
            }
            writer.stepOut()
        }

        override fun writeUnionBA(value: Kitchen.UnionB.A) {
            writer.stepIn(IonType.SEXP)
            writer.writeBool(value.x)
            writer.writeInt(value.y)
            writer.writeString(value.z)
            writer.stepOut()
        }

        override fun writeUnionBB(value: Kitchen.UnionB.B) {
            writer.stepIn(IonType.SEXP)
            writeArray(value.x, null) { writer.writeBool(it) }
            writeArray(value.y, null) { writer.writeInt(it) }
            writeArray(value.z, null) { writer.writeString(it) }
            writer.stepOut()
        }

        override fun writeUnionBC(value: Kitchen.UnionB.C) {
            writer.stepIn(IonType.SEXP)
            writeDecimal(value.x, 1, 2)
            writeBlob(value.y, 10)
            writeClob(value.z, 10)
            writer.stepOut()
        }

        override fun writeUnionC(value: Kitchen.UnionC) {
            writer.stepIn(IonType.SEXP)
            when (value) {
                is Kitchen.UnionC.A -> {
                    writer.writeInt(0L)
                    writeUnionCA(value)
                }
                is Kitchen.UnionC.B -> {
                    writer.writeInt(1L)
                    writeUnionCB(value)
                }
            }
            writer.stepOut()
        }

        override fun writeUnionCA(value: Kitchen.UnionC.A) {
            writer.stepIn(IonType.SEXP)
            when (value) {
                is Kitchen.UnionC.A.A -> {
                    writer.writeInt(0L)
                    writeUnionCAA(value)
                }
                is Kitchen.UnionC.A.B -> {
                    writer.writeInt(1L)
                    writeUnionCAB(value)
                }
                is Kitchen.UnionC.A.C -> {
                    writer.writeInt(2L)
                    writeUnionCAC(value)
                }
            }
            writer.stepOut()
        }

        override fun writeUnionCAA(value: Kitchen.UnionC.A.A) {
            writer.writeBool(value.value)
        }

        override fun writeUnionCAB(value: Kitchen.UnionC.A.B) {
            writer.writeInt(value.value)
        }

        override fun writeUnionCAC(value: Kitchen.UnionC.A.C) {
            writer.writeString(value.value)
        }

        override fun writeUnionCB(value: Kitchen.UnionC.B) {
            writer.stepIn(IonType.SEXP)
            when (value) {
                is Kitchen.UnionC.B.A -> {
                    writer.writeInt(0L)
                    writeUnionCBA(value)
                }
                is Kitchen.UnionC.B.B -> {
                    writer.writeInt(1L)
                    writeUnionCBB(value)
                }
                is Kitchen.UnionC.B.C -> {
                    writer.writeInt(2L)
                    writeUnionCBC(value)
                }
            }
            writer.stepOut()
        }

        override fun writeUnionCBA(value: Kitchen.UnionC.B.A) {
            writeArray(value.values, null) { writer.writeBool(it) }
        }

        override fun writeUnionCBB(value: Kitchen.UnionC.B.B) {
            writeArray(value.values, null) { writer.writeInt(it) }
        }

        override fun writeUnionCBC(value: Kitchen.UnionC.B.C) {
            writeArray(value.values, null) { writer.writeString(it) }
        }
    }
}

public abstract class KitchenReader(reader: IonReader) : RidlReader(reader) {

    public abstract fun readBoxBool(): Kitchen.BoxBool

    public abstract fun readBoxInt(): Kitchen.BoxInt

    public abstract fun readBoxFloat(): Kitchen.BoxFloat

    public abstract fun readBoxDecimal(): Kitchen.BoxDecimal

    public abstract fun readBoxStr(): Kitchen.BoxStr

    public abstract fun readBoxBlob(): Kitchen.BoxBlob

    public abstract fun readBoxClob(): Kitchen.BoxClob

    public abstract fun readDecimalA(): Kitchen.DecimalA

    public abstract fun readDecimalB(): Kitchen.DecimalB

    public abstract fun readDecimalC(): Kitchen.DecimalC

    public abstract fun readBlobA(): Kitchen.BlobA

    public abstract fun readClobB(): Kitchen.ClobB

    public abstract fun readArrA(): Kitchen.ArrA

    public abstract fun readArrB(): Kitchen.ArrB

    public abstract fun readArrC(): Kitchen.ArrC

    public abstract fun readArrD(): Kitchen.ArrD

    public abstract fun readArrNested(): Kitchen.ArrNested

    public abstract fun readEnumA(): Kitchen.EnumA

    public abstract fun readNil(): Kitchen.Nil

    public abstract fun readStructA(): Kitchen.StructA

    public abstract fun readStructAlias(): Kitchen.StructAlias

    public abstract fun readStructB(): Kitchen.StructB

    public abstract fun readStructC(): Kitchen.StructC

    public abstract fun readUnionA(): Kitchen.UnionA

    public abstract fun readUnionAA(): Kitchen.UnionA.A

    public abstract fun readUnionAB(): Kitchen.UnionA.B

    public abstract fun readUnionAC(): Kitchen.UnionA.C

    public abstract fun readUnionB(): Kitchen.UnionB

    public abstract fun readUnionBA(): Kitchen.UnionB.A

    public abstract fun readUnionBB(): Kitchen.UnionB.B

    public abstract fun readUnionBC(): Kitchen.UnionB.C

    public abstract fun readUnionC(): Kitchen.UnionC

    public abstract fun readUnionCA(): Kitchen.UnionC.A

    public abstract fun readUnionCAA(): Kitchen.UnionC.A.A

    public abstract fun readUnionCAB(): Kitchen.UnionC.A.B

    public abstract fun readUnionCAC(): Kitchen.UnionC.A.C

    public abstract fun readUnionCB(): Kitchen.UnionC.B

    public abstract fun readUnionCBA(): Kitchen.UnionC.B.A

    public abstract fun readUnionCBB(): Kitchen.UnionC.B.B

    public abstract fun readUnionCBC(): Kitchen.UnionC.B.C

    public companion object {

        @JvmStatic
        public fun text(input: InputStream): KitchenReader = Text(IonReaderBuilder.standard().build(input))

        @JvmStatic
        public fun text(reader: IonReader): KitchenReader = Text(reader)

        @JvmStatic
        public fun packed(input: InputStream): KitchenReader = Packed(IonReaderBuilder.standard().build(input))

        @JvmStatic
        public fun packed(reader: IonReader): KitchenReader = Packed(reader)
    }

    private class Text(reader: IonReader) : KitchenReader(reader) {

        override fun readBoxBool(): Kitchen.BoxBool {
            assertType(IonType.BOOL, "box_bool")
            val value = reader.booleanValue()
            return Kitchen.BoxBool(value)
        }

        override fun readBoxInt(): Kitchen.BoxInt {
            assertType(IonType.INT, "box_int")
            val value = reader.longValue()
            return Kitchen.BoxInt(value)
        }

        override fun readBoxFloat(): Kitchen.BoxFloat {
            assertType(IonType.FLOAT, "box_float")
            val value = reader.doubleValue()
            return Kitchen.BoxFloat(value)
        }

        override fun readBoxDecimal(): Kitchen.BoxDecimal {
            assertType(IonType.DECIMAL, "box_decimal")
            val value = reader.decimalValue()
            return Kitchen.BoxDecimal(value)
        }

        override fun readBoxStr(): Kitchen.BoxStr {
            assertType(IonType.STRING, "box_str")
            val value = reader.stringValue()
            return Kitchen.BoxStr(value)
        }

        override fun readBoxBlob(): Kitchen.BoxBlob {
            assertType(IonType.BLOB, "box_blob")
            val value = reader.newBytes()
            return Kitchen.BoxBlob(value)
        }

        override fun readBoxClob(): Kitchen.BoxClob {
            assertType(IonType.CLOB, "box_clob")
            val value = reader.newBytes()
            return Kitchen.BoxClob(value)
        }

        override fun readDecimalA(): Kitchen.DecimalA {
            assertType(IonType.DECIMAL, "decimal_a")
            val value = decimalValue(1, 2)
            return Kitchen.DecimalA(value)
        }

        override fun readDecimalB(): Kitchen.DecimalB {
            assertType(IonType.DECIMAL, "decimal_b")
            val value = decimalValue(1, null)
            return Kitchen.DecimalB(value)
        }

        override fun readDecimalC(): Kitchen.DecimalC {
            assertType(IonType.DECIMAL, "decimal_c")
            val value = decimalValue(null, 2)
            return Kitchen.DecimalC(value)
        }

        override fun readBlobA(): Kitchen.BlobA {
            assertType(IonType.BLOB, "blob_a")
            val value = newBytes(1)
            return Kitchen.BlobA(value)
        }

        override fun readClobB(): Kitchen.ClobB {
            assertType(IonType.CLOB, "clob_b")
            val value = newBytes(1)
            return Kitchen.ClobB(value)
        }

        override fun readArrA(): Kitchen.ArrA {
            assertType(IonType.LIST, "arr_a")
            val values = readArray { reader.booleanValue() }
            return Kitchen.ArrA(values)
        }

        override fun readArrB(): Kitchen.ArrB {
            assertType(IonType.LIST, "arr_b")
            val values = readArray(10) { reader.booleanValue() }
            return Kitchen.ArrB(values)
        }

        override fun readArrC(): Kitchen.ArrC {
            assertType(IonType.LIST, "arr_c")
            val values = readArray { readBoxBool() }
            return Kitchen.ArrC(values)
        }

        override fun readArrD(): Kitchen.ArrD {
            assertType(IonType.LIST, "arr_d")
            val values = readArray(10) { readBoxBool() }
            return Kitchen.ArrD(values)
        }

        override fun readArrNested(): Kitchen.ArrNested {
            assertType(IonType.LIST, "arr_nested")
            val values = readArray { readArray() { reader.booleanValue() } }
            return Kitchen.ArrNested(values)
        }

        override fun readEnumA(): Kitchen.EnumA {
            assertType(IonType.SYMBOL, "enum_a")
            return try {
                Kitchen.EnumA.valueOf(reader.symbolValue().text)
            } catch (ex: IllegalArgumentException) {
                // TODO error messaging
                throw ex
            }
        }

        override fun readNil(): Kitchen.Nil {
            assertType(IonType.SYMBOL, "nil")
            assertUnit()
            return Kitchen.Nil
        }

        override fun readStructA(): Kitchen.StructA {
            assertType(IonType.STRUCT, "struct_a")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = reader.booleanValue()
            assertField("y"); reader.next()
            val y = reader.longValue()
            assertField("z"); reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.StructA(x, y, z)
        }

        override fun readStructAlias(): Kitchen.StructAlias {
            assertType(IonType.STRUCT, "struct_alias")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = reader.booleanValue()
            assertField("y"); reader.next()
            val y = reader.longValue()
            assertField("z"); reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.StructAlias(x, y, z)
        }

        override fun readStructB(): Kitchen.StructB {
            assertType(IonType.STRUCT, "struct_b")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = readArray() { reader.booleanValue() }
            assertField("y"); reader.next()
            val y = readArray() { reader.longValue() }
            assertField("z"); reader.next()
            val z = readArray() { reader.stringValue() }
            reader.stepOut()
            return Kitchen.StructB(x, y, z)
        }

        override fun readStructC(): Kitchen.StructC {
            assertType(IonType.STRUCT, "struct_c")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = decimalValue(1, 2)
            assertField("y"); reader.next()
            val y = newBytes(10)
            assertField("z"); reader.next()
            val z = newBytes(10)
            reader.stepOut()
            return Kitchen.StructC(x, y, z)
        }

        override fun readUnionA(): Kitchen.UnionA {
            val tag = reader.typeAnnotations[0]
            return when (tag) {
                "union_a.a" -> readUnionAA()
                "union_a.b" -> readUnionAB()
                "union_a.c" -> readUnionAC()
                else -> error("Unexpected union tag `$tag` for union `union_a`")
            }
        }

        override fun readUnionAA(): Kitchen.UnionA.A {
            assertType(IonType.BOOL, "union_a.a")
            val value = reader.booleanValue()
            return Kitchen.UnionA.A(value)
        }

        override fun readUnionAB(): Kitchen.UnionA.B {
            assertType(IonType.INT, "union_a.b")
            val value = reader.longValue()
            return Kitchen.UnionA.B(value)
        }

        override fun readUnionAC(): Kitchen.UnionA.C {
            assertType(IonType.STRING, "union_a.c")
            val value = reader.stringValue()
            return Kitchen.UnionA.C(value)
        }

        override fun readUnionB(): Kitchen.UnionB {
            val tag = reader.typeAnnotations[0]
            return when (tag) {
                "union_b.a" -> readUnionBA()
                "union_b.b" -> readUnionBB()
                "union_b.c" -> readUnionBC()
                else -> error("Unexpected union tag `$tag` for union `union_b`")
            }
        }

        override fun readUnionBA(): Kitchen.UnionB.A {
            assertType(IonType.STRUCT, "union_b.a")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = reader.booleanValue()
            assertField("y"); reader.next()
            val y = reader.longValue()
            assertField("z"); reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.UnionB.A(x, y, z)
        }

        override fun readUnionBB(): Kitchen.UnionB.B {
            assertType(IonType.STRUCT, "union_b.b")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = readArray() { reader.booleanValue() }
            assertField("y"); reader.next()
            val y = readArray() { reader.longValue() }
            assertField("z"); reader.next()
            val z = readArray() { reader.stringValue() }
            reader.stepOut()
            return Kitchen.UnionB.B(x, y, z)
        }

        override fun readUnionBC(): Kitchen.UnionB.C {
            assertType(IonType.STRUCT, "union_b.c")
            reader.stepIn()
            assertField("x"); reader.next()
            val x = decimalValue(1, 2)
            assertField("y"); reader.next()
            val y = newBytes(10)
            assertField("z"); reader.next()
            val z = newBytes(10)
            reader.stepOut()
            return Kitchen.UnionB.C(x, y, z)
        }

        override fun readUnionC(): Kitchen.UnionC {
            val tag = reader.typeAnnotations[0]
            return when (tag) {
                "union_c.a" -> readUnionCA()
                "union_c.b" -> readUnionCB()
                else -> error("Unexpected union tag `$tag` for union `union_c`")
            }
        }

        override fun readUnionCA(): Kitchen.UnionC.A {
            val tag = reader.typeAnnotations[0]
            return when (tag) {
                "union_c.a.a" -> readUnionCAA()
                "union_c.a.b" -> readUnionCAB()
                "union_c.a.c" -> readUnionCAC()
                else -> error("Unexpected union tag `$tag` for union `union_c.a`")
            }
        }

        override fun readUnionCAA(): Kitchen.UnionC.A.A {
            assertType(IonType.BOOL, "union_c.a.a")
            val value = reader.booleanValue()
            return Kitchen.UnionC.A.A(value)
        }

        override fun readUnionCAB(): Kitchen.UnionC.A.B {
            assertType(IonType.INT, "union_c.a.b")
            val value = reader.longValue()
            return Kitchen.UnionC.A.B(value)
        }

        override fun readUnionCAC(): Kitchen.UnionC.A.C {
            assertType(IonType.STRING, "union_c.a.c")
            val value = reader.stringValue()
            return Kitchen.UnionC.A.C(value)
        }

        override fun readUnionCB(): Kitchen.UnionC.B {
            val tag = reader.typeAnnotations[0]
            return when (tag) {
                "union_c.b.a" -> readUnionCBA()
                "union_c.b.b" -> readUnionCBB()
                "union_c.b.c" -> readUnionCBC()
                else -> error("Unexpected union tag `$tag` for union `union_c.b`")
            }
        }

        override fun readUnionCBA(): Kitchen.UnionC.B.A {
            assertType(IonType.LIST, "union_c.b.a")
            val values = readArray { reader.booleanValue() }
            return Kitchen.UnionC.B.A(values)
        }

        override fun readUnionCBB(): Kitchen.UnionC.B.B {
            assertType(IonType.LIST, "union_c.b.b")
            val values = readArray { reader.longValue() }
            return Kitchen.UnionC.B.B(values)
        }

        override fun readUnionCBC(): Kitchen.UnionC.B.C {
            assertType(IonType.LIST, "union_c.b.c")
            val values = readArray { reader.stringValue() }
            return Kitchen.UnionC.B.C(values)
        }
    }

    private class Packed(reader: IonReader) : KitchenReader(reader) {

        override fun readBoxBool(): Kitchen.BoxBool {
            assertType(IonType.BOOL)
            val value = reader.booleanValue()
            return Kitchen.BoxBool(value)
        }

        override fun readBoxInt(): Kitchen.BoxInt {
            assertType(IonType.INT)
            val value = reader.longValue()
            return Kitchen.BoxInt(value)
        }

        override fun readBoxFloat(): Kitchen.BoxFloat {
            assertType(IonType.FLOAT)
            val value = reader.doubleValue()
            return Kitchen.BoxFloat(value)
        }

        override fun readBoxDecimal(): Kitchen.BoxDecimal {
            assertType(IonType.DECIMAL)
            val value = reader.decimalValue()
            return Kitchen.BoxDecimal(value)
        }

        override fun readBoxStr(): Kitchen.BoxStr {
            assertType(IonType.STRING)
            val value = reader.stringValue()
            return Kitchen.BoxStr(value)
        }

        override fun readBoxBlob(): Kitchen.BoxBlob {
            assertType(IonType.BLOB)
            val value = reader.newBytes()
            return Kitchen.BoxBlob(value)
        }

        override fun readBoxClob(): Kitchen.BoxClob {
            assertType(IonType.CLOB)
            val value = reader.newBytes()
            return Kitchen.BoxClob(value)
        }

        override fun readDecimalA(): Kitchen.DecimalA {
            assertType(IonType.DECIMAL)
            val value = decimalValue(1, 2)
            return Kitchen.DecimalA(value)
        }

        override fun readDecimalB(): Kitchen.DecimalB {
            assertType(IonType.DECIMAL)
            val value = decimalValue(1, null)
            return Kitchen.DecimalB(value)
        }

        override fun readDecimalC(): Kitchen.DecimalC {
            assertType(IonType.DECIMAL)
            val value = decimalValue(null, 2)
            return Kitchen.DecimalC(value)
        }

        override fun readBlobA(): Kitchen.BlobA {
            assertType(IonType.BLOB)
            val value = newBytes(1)
            return Kitchen.BlobA(value)
        }

        override fun readClobB(): Kitchen.ClobB {
            assertType(IonType.CLOB)
            val value = newBytes(1)
            return Kitchen.ClobB(value)
        }

        override fun readArrA(): Kitchen.ArrA {
            assertType(IonType.LIST)
            val values = readArray { reader.booleanValue() }
            return Kitchen.ArrA(values)
        }

        override fun readArrB(): Kitchen.ArrB {
            assertType(IonType.LIST)
            val values = readArray(10) { reader.booleanValue() }
            return Kitchen.ArrB(values)
        }

        override fun readArrC(): Kitchen.ArrC {
            assertType(IonType.LIST)
            val values = readArray { readBoxBool() }
            return Kitchen.ArrC(values)
        }

        override fun readArrD(): Kitchen.ArrD {
            assertType(IonType.LIST)
            val values = readArray(10) { readBoxBool() }
            return Kitchen.ArrD(values)
        }

        override fun readArrNested(): Kitchen.ArrNested {
            assertType(IonType.LIST)
            val values = readArray { readArray() { reader.booleanValue() } }
            return Kitchen.ArrNested(values)
        }

        override fun readEnumA(): Kitchen.EnumA {
            assertType(IonType.SYMBOL)
            return try {
                Kitchen.EnumA.valueOf(reader.symbolValue().text)
            } catch (ex: IllegalArgumentException) {
                // TODO error messaging
                throw ex
            }
        }

        override fun readNil(): Kitchen.Nil {
            assertType(IonType.SYMBOL)
            assertUnit()
            return Kitchen.Nil
        }

        override fun readStructA(): Kitchen.StructA {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = reader.booleanValue()
            reader.next()
            val y = reader.longValue()
            reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.StructA(x, y, z)
        }

        override fun readStructAlias(): Kitchen.StructAlias {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = reader.booleanValue()
            reader.next()
            val y = reader.longValue()
            reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.StructAlias(x, y, z)
        }

        override fun readStructB(): Kitchen.StructB {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = readArray() { reader.booleanValue() }
            reader.next()
            val y = readArray() { reader.longValue() }
            reader.next()
            val z = readArray() { reader.stringValue() }
            reader.stepOut()
            return Kitchen.StructB(x, y, z)
        }

        override fun readStructC(): Kitchen.StructC {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = decimalValue(1, 2)
            reader.next()
            val y = newBytes(10)
            reader.next()
            val z = newBytes(10)
            reader.stepOut()
            return Kitchen.StructC(x, y, z)
        }

        override fun readUnionA(): Kitchen.UnionA {
            assertType(IonType.SEXP)
            reader.stepIn()
            val tag = reader.intValue()
            val variant = when (tag) {
                0 -> readUnionAA()
                1 -> readUnionAB()
                2 -> readUnionAC()
                else -> error("Unexpected union tag `$tag`, expected 0-2")
            }
            reader.stepOut()
            return variant
        }

        override fun readUnionAA(): Kitchen.UnionA.A {
            assertType(IonType.BOOL)
            val value = reader.booleanValue()
            return Kitchen.UnionA.A(value)
        }

        override fun readUnionAB(): Kitchen.UnionA.B {
            assertType(IonType.INT)
            val value = reader.longValue()
            return Kitchen.UnionA.B(value)
        }

        override fun readUnionAC(): Kitchen.UnionA.C {
            assertType(IonType.STRING)
            val value = reader.stringValue()
            return Kitchen.UnionA.C(value)
        }

        override fun readUnionB(): Kitchen.UnionB {
            assertType(IonType.SEXP)
            reader.stepIn()
            val tag = reader.intValue()
            val variant = when (tag) {
                0 -> readUnionBA()
                1 -> readUnionBB()
                2 -> readUnionBC()
                else -> error("Unexpected union tag `$tag`, expected 0-2")
            }
            reader.stepOut()
            return variant
        }

        override fun readUnionBA(): Kitchen.UnionB.A {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = reader.booleanValue()
            reader.next()
            val y = reader.longValue()
            reader.next()
            val z = reader.stringValue()
            reader.stepOut()
            return Kitchen.UnionB.A(x, y, z)
        }

        override fun readUnionBB(): Kitchen.UnionB.B {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = readArray() { reader.booleanValue() }
            reader.next()
            val y = readArray() { reader.longValue() }
            reader.next()
            val z = readArray() { reader.stringValue() }
            reader.stepOut()
            return Kitchen.UnionB.B(x, y, z)
        }

        override fun readUnionBC(): Kitchen.UnionB.C {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val x = decimalValue(1, 2)
            reader.next()
            val y = newBytes(10)
            reader.next()
            val z = newBytes(10)
            reader.stepOut()
            return Kitchen.UnionB.C(x, y, z)
        }

        override fun readUnionC(): Kitchen.UnionC {
            assertType(IonType.SEXP)
            reader.stepIn()
            val tag = reader.intValue()
            val variant = when (tag) {
                0 -> readUnionCA()
                1 -> readUnionCB()
                else -> error("Unexpected union tag `$tag`, expected 0-1")
            }
            reader.stepOut()
            return variant
        }

        override fun readUnionCA(): Kitchen.UnionC.A {
            assertType(IonType.SEXP)
            reader.stepIn()
            val tag = reader.intValue()
            val variant = when (tag) {
                0 -> readUnionCAA()
                1 -> readUnionCAB()
                2 -> readUnionCAC()
                else -> error("Unexpected union tag `$tag`, expected 0-2")
            }
            reader.stepOut()
            return variant
        }

        override fun readUnionCAA(): Kitchen.UnionC.A.A {
            assertType(IonType.BOOL)
            val value = reader.booleanValue()
            return Kitchen.UnionC.A.A(value)
        }

        override fun readUnionCAB(): Kitchen.UnionC.A.B {
            assertType(IonType.INT)
            val value = reader.longValue()
            return Kitchen.UnionC.A.B(value)
        }

        override fun readUnionCAC(): Kitchen.UnionC.A.C {
            assertType(IonType.STRING)
            val value = reader.stringValue()
            return Kitchen.UnionC.A.C(value)
        }

        override fun readUnionCB(): Kitchen.UnionC.B {
            assertType(IonType.SEXP)
            reader.stepIn()
            val tag = reader.intValue()
            val variant = when (tag) {
                0 -> readUnionCBA()
                1 -> readUnionCBB()
                2 -> readUnionCBC()
                else -> error("Unexpected union tag `$tag`, expected 0-2")
            }
            reader.stepOut()
            return variant
        }

        override fun readUnionCBA(): Kitchen.UnionC.B.A {
            assertType(IonType.LIST)
            val values = readArray { reader.booleanValue() }
            return Kitchen.UnionC.B.A(values)
        }

        override fun readUnionCBB(): Kitchen.UnionC.B.B {
            assertType(IonType.LIST)
            val values = readArray { reader.longValue() }
            return Kitchen.UnionC.B.B(values)
        }

        override fun readUnionCBC(): Kitchen.UnionC.B.C {
            assertType(IonType.LIST)
            val values = readArray { reader.stringValue() }
            return Kitchen.UnionC.B.C(values)
        }
    }
}

