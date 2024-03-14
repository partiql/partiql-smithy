package org.partiql.tool.ridl.codegen.language.isl

import org.partiql.tool.ridl.model.Document
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "isl",
    mixinStandardHelpOptions = true,
    description = ["Generates IonSchema sources"]
)
internal class IslCommand : Callable<Int> {

    @CommandLine.Parameters(
        index = "0",
        description = ["Type definition file"]
    )
    lateinit var file: File

    override fun call(): Int {
        val input = file.readText()
        val document = Document.load(input)
        IslGenerator.generate(document)
        return 0
    }
}
