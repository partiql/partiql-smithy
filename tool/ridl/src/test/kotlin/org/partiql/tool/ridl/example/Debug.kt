package org.partiql.tool.ridl.example

import com.amazon.ion.system.IonTextWriterBuilder
import org.junit.jupiter.api.Test

class Debug {

    @Test
    fun debug() {
        val out = StringBuilder()
        val writer = IonTextWriterBuilder.pretty().build(out)
        val pos = Pos(1, 2, 3)
        pos.write(writer)
        println(out)
    }
}
