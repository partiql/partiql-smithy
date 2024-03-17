package org.partiql.tool.ridl.coverage

import com.amazon.ion.*
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import com.example.ridl.Coverage
import com.example.ridl.IonSerializable
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import java.io.File
import java.util.stream.Stream
import kotlin.io.path.toPath
import kotlin.test.assertEquals

class TestKotlinCoverage {

    private val root = this::class.java.getResource("/cases")!!.toURI().toPath()
    private val ion = IonSystemBuilder.standard().build()

    // type to read functions
    private val readers = mapOf<String, (IonReader) -> IonSerializable>(
        // primitives
        "t_bool" to Coverage.TBool::read,
        "t_i32" to Coverage.TI32::read,
        "t_i64" to Coverage.TI64::read,
        "t_f32" to Coverage.TF32::read,
        "t_f64" to Coverage.TF64::read,
        "t_str" to Coverage.TStr::read,
        "t_byte" to Coverage.TByte::read,
        "t_bytes" to Coverage.TBytes::read,
        // arrays
        "t_array_prim_var" to Coverage.TArrayPrimVar::read,
        "t_array_prim_fix" to Coverage.TArrayPrimFix::read,
        "t_array_var" to Coverage.TArrayVar::read,
        "t_array_fix" to Coverage.TArrayFix::read,
        // structs
        "t_struct_prim" to Coverage.TStructPrim::read,
    )

    @TestFactory
    fun coverage(): Stream<DynamicNode> {
        return root.toFile()
            .listFiles { f -> f.isDirectory }!!
            .mapNotNull { load(it) }
            .stream()
    }

    private fun load(file: File): DynamicNode? = when {
        file.isDirectory -> loadD(file)
        file.extension == "ion" -> loadF(file)
        else -> null
    }

    private fun loadD(file: File): DynamicContainer {
        val name = file.nameWithoutExtension
        val children = file.listFiles()!!.map { load(it) }
        return dynamicContainer(name, children)
    }

    private fun loadF(file: File): DynamicContainer {
        val name = file.nameWithoutExtension
        val children = loadT(file)
        return dynamicContainer(name, children)
    }

    private fun loadT(file: File): List<DynamicTest> {
        return ion.iterate(file.reader())
            .asSequence()
            .mapIndexed { i, v -> test(i, v as IonStruct) }
            .toList()
    }

    // round-trip test
    private fun test(index: Int, struct: IonStruct): DynamicTest {
        val type = (struct.get("type") as IonText).stringValue()
        val assert = (struct.get("assert") as IonText).stringValue()
        val value = (struct.get("value"))
        println(value.type)
        println(value.toString())
        println("------")
        return DynamicTest.dynamicTest("$type[$index]: $assert") {
            val read = readers[type]
            if (read == null) {
                assumeTrue(false, "no reader for $type")
                return@dynamicTest
            }
            // Read
            val reader = IonReaderBuilder.standard().build(value)
            reader.next()
            when (assert) {
                "success" -> {
                    // Read (success)
                    val message = read(reader)
                    // Write
                    val sb = StringBuilder()
                    val writer = IonTextWriterBuilder.standard().build(sb)
                    message.write(writer)
                    // Round-trip equal
                    assertEquals(value, ion.singleValue(sb.toString()))
                }
                "failure" -> {
                    // Read (failure)
                    assertThrows<Throwable> { read(reader) }
                }
            }
        }
    }
}
