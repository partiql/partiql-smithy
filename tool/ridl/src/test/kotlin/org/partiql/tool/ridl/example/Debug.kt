package org.partiql.tool.ridl.example

import com.amazon.ion.system.IonTextWriterBuilder
import org.junit.jupiter.api.Test
import org.partiql.tool.ridl.codegen.language.kotlin.example.Example

class Debug {

    @Test
    fun debug() {
        val out = StringBuilder()
        val writer = IonTextWriterBuilder.pretty().build(out)
        val pos = Example.Position(1, 2, 3)
        pos.write(writer)
        println(out)
    }
}
