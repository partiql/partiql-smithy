package org.partiql.tool.ridl.example

import com.amazon.ion.system.IonTextWriterBuilder
import com.example.ridl.Coverage
import org.junit.jupiter.api.Test

class Debug {

    @Test
    fun debug() {
//        val reader = IonReaderBuilder.standard().build("")
//        val byte = reader.newBytes()[0]
        val out = StringBuilder()
        val writer = IonTextWriterBuilder.pretty().build(out)

//        // Structs (basic)
//        val pos = Coverage.Position(1, 2, 3)
//        pos.write(writer)
//
//        // Structs (recursive)
//        val lat = Coverage.Decimal(12, 1)
//        val lon = Coverage.Decimal(100, 2)
//        val coordinates = Coverage.Coordinates(lat, lon)
//        coordinates.write(writer)
//
//        // Enums
//        val asc = Coverage.Order.ASC
//        asc.write(writer)
//        val desc = Coverage.Order.DESC
//        desc.write(writer)
//
//        // Arrays
//        val arrayFixed = Coverage.ArrayFixed(arrayOf(1, 2, 3))
//        arrayFixed.write(writer)
//        val arrayVariable = Coverage.ArrayFixed(arrayOf(7, 8, 9))
//        arrayVariable.write(writer)
//
//        // Units
//        val singleton = Coverage.Singleton
//        singleton.write(writer)

        println(out)
    }
}
