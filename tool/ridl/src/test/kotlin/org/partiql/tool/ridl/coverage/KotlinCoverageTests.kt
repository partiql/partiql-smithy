package org.partiql.tool.ridl.coverage

import com.amazon.ion.*
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ion.util.IonValueUtils
import com.example.ridl.Coverage
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.stream.Stream
import kotlin.io.path.toPath

typealias Read = (IonReader) -> Any

data class CoverageTest(
    @JvmField val type: String,
    @JvmField val assert: String,
    @JvmField val ion: IonValue,
)

class KotlinCoverageTests {

    private val root = this::class.java.getResource("/cases")!!.toURI().toPath()
    private val ion = IonSystemBuilder.standard().build()

    // type to read functions
    private val readers = mapOf<String, Read>(
        "t_i32" to Coverage.TI32::read,
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
        val ion = (struct.get("ion"))
        return DynamicTest.dynamicTest("$type[$index]: $assert") {
            val reader = readers[type]
            if (reader == null) {
                assumeTrue(false, "no reader for $type")
            }
            println(ion.toPrettyString())
        }
    }
}
