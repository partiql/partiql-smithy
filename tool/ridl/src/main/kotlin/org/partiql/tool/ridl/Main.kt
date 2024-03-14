/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.partiql.tool.ridl

import com.amazon.ion.system.IonTextWriterBuilder
import org.partiql.tool.ridl.codegen.language.isl.IslCommand
import org.partiql.tool.ridl.codegen.language.kotlin.KotlinCommand
import org.partiql.tool.ridl.codegen.language.kotlin.example.Example
import picocli.CommandLine
import kotlin.system.exitProcess

public fun main(args: Array<String>) {
    val command = CommandLine(RIDL())
    exitProcess(command.execute(*args))
}

@CommandLine.Command(
    name = "ridl",
    mixinStandardHelpOptions = true,
    subcommands = [Generate::class],
)
public class RIDL : Runnable {
    override fun run() {}
}

@CommandLine.Command(
    name = "generate",
    mixinStandardHelpOptions = true,
    subcommands = [
        IslCommand::class,
        KotlinCommand::class,
    ],
)
public class Generate : Runnable {

    override fun run() {

        // Structs (basic)
        val out = StringBuilder()
//        val writer = IonTextWriterBuilder.pretty().build(out)
        val writer = IonTextWriterBuilder.standard().build(out)
        val pos = Example.Position(1, 2, 3)
        pos.write(writer)

        // Structs (recursive)
        val lat = Example.Decimal(12, 1)
        val lon = Example.Decimal(100, 2)
        val coordinates = Example.Coordinates(lat, lon)
        coordinates.write(writer)

        // Enums
        val asc = Example.Order.ASC
        asc.write(writer)
        val desc = Example.Order.DESC
        desc.write(writer)

        // Arrays
        val arrayFixed = Example.ArrayFixed(arrayOf(1, 2, 3))
        arrayFixed.write(writer)
        val arrayVariable = Example.ArrayFixed(arrayOf(7, 8, 9))
        arrayVariable.write(writer)

        // Units
        val singleton = Example.Singleton
        singleton.write(writer)

        println(out)
    }
}
