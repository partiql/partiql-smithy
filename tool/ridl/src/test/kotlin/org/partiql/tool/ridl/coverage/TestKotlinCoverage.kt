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

    // type to `read` functions
    private val readers = mapOf<String, (IonReader) -> IonSerializable>(
        // arrays
        "t_array_prim_var" to Coverage.TArrayPrimVar::read,
        "t_array_prim_fix" to Coverage.TArrayPrimFix::read,
        "t_array_var" to Coverage.TArrayVar::read,
        "t_array_fix" to Coverage.TArrayFix::read,
        // enums
        //
        // structs
        "t_struct_prim" to Coverage.TStructPrim::read,
        "t_struct" to Coverage.TStruct::read,
        // unions
        "t_union_refs" to Coverage.TUnionRefs::read,
        "t_union" to Coverage.TUnion::read,
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
